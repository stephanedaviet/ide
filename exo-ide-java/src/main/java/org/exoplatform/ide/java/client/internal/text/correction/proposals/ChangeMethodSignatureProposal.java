/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.exoplatform.ide.java.client.internal.text.correction.proposals;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.java.client.core.dom.AST;
import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.core.dom.IBinding;
import org.exoplatform.ide.java.client.core.dom.IMethodBinding;
import org.exoplatform.ide.java.client.core.dom.ITypeBinding;
import org.exoplatform.ide.java.client.core.dom.IVariableBinding;
import org.exoplatform.ide.java.client.core.dom.Javadoc;
import org.exoplatform.ide.java.client.core.dom.MethodDeclaration;
import org.exoplatform.ide.java.client.core.dom.Name;
import org.exoplatform.ide.java.client.core.dom.SimpleName;
import org.exoplatform.ide.java.client.core.dom.SingleVariableDeclaration;
import org.exoplatform.ide.java.client.core.dom.TagElement;
import org.exoplatform.ide.java.client.core.dom.TextElement;
import org.exoplatform.ide.java.client.core.dom.Type;
import org.exoplatform.ide.java.client.core.dom.rewrite.ASTRewrite;
import org.exoplatform.ide.java.client.core.dom.rewrite.ImportRewrite;
import org.exoplatform.ide.java.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.exoplatform.ide.java.client.core.dom.rewrite.ListRewrite;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.ASTResolving;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.StubUtility;
import org.exoplatform.ide.java.client.internal.corext.dom.ASTNodeFactory;
import org.exoplatform.ide.java.client.internal.corext.dom.ASTNodes;
import org.exoplatform.ide.java.client.internal.corext.dom.Bindings;
import org.exoplatform.ide.java.client.internal.corext.dom.LinkedNodeFinder;
import org.exoplatform.ide.java.client.internal.corext.dom.ScopeAnalyzer;
import org.exoplatform.ide.java.client.internal.text.correction.JavadocTagsSubProcessor;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.runtime.CoreException;
import org.exoplatform.ide.text.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChangeMethodSignatureProposal extends LinkedCorrectionProposal
{

   public static interface ChangeDescription
   {
   }

   public static class SwapDescription implements ChangeDescription
   {
      final int index;

      public SwapDescription(int index)
      {
         this.index = index;
      }
   }

   public static class RemoveDescription implements ChangeDescription
   {
   }

   static class ModifyDescription implements ChangeDescription
   {
      public final String name;

      public final ITypeBinding type;

      Type resultingParamType;

      SimpleName[] resultingParamName;

      SimpleName resultingTagArg;

      private ModifyDescription(ITypeBinding type, String name)
      {
         this.type = type;
         this.name = name;
      }
   }

   public static class EditDescription extends ModifyDescription
   {
      String orginalName;

      public EditDescription(ITypeBinding type, String name)
      {
         super(type, name);
      }
   }

   public static class InsertDescription extends ModifyDescription
   {
      public InsertDescription(ITypeBinding type, String name)
      {
         super(type, name);
      }
   }

   private ASTNode fInvocationNode;

   private IMethodBinding fSenderBinding;

   private ChangeDescription[] fParameterChanges;

   private ChangeDescription[] fExceptionChanges;

   public ChangeMethodSignatureProposal(String label, ASTNode invocationNode, IMethodBinding binding,
      ChangeDescription[] paramChanges, ChangeDescription[] exceptionChanges, int relevance, Document document,
      Image image)
   {
      super(label, null, relevance, document, image);

      Assert.isTrue(binding != null && Bindings.isDeclarationBinding(binding));

      fInvocationNode = invocationNode;
      fSenderBinding = binding;
      fParameterChanges = paramChanges;
      fExceptionChanges = exceptionChanges;
   }

   @Override
   protected ASTRewrite getRewrite() throws CoreException
   {
      CompilationUnit astRoot = (CompilationUnit)fInvocationNode.getRoot();
      ASTNode methodDecl = astRoot.findDeclaringNode(fSenderBinding);
      ASTNode newMethodDecl = null;
      if (methodDecl != null)
      {
         newMethodDecl = methodDecl;
      }
      else
      {
         astRoot = ASTResolving.createQuickFixAST(document);
         newMethodDecl = astRoot.findDeclaringNode(fSenderBinding.getKey());
      }
      createImportRewrite(astRoot);

      if (newMethodDecl instanceof MethodDeclaration)
      {
         MethodDeclaration decl = (MethodDeclaration)newMethodDecl;

         ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());
         if (fParameterChanges != null)
         {
            modifyParameters(rewrite, decl);
         }
         if (fExceptionChanges != null)
         {
            modifyExceptions(rewrite, decl);
         }
         return rewrite;
      }
      return null;
   }

   private void modifyParameters(ASTRewrite rewrite, MethodDeclaration methodDecl)
   {
      AST ast = methodDecl.getAST();

      ArrayList<String> usedNames = new ArrayList<String>();
      boolean hasCreatedVariables = false;

      IVariableBinding[] declaredFields = fSenderBinding.getDeclaringClass().getDeclaredFields();
      for (int i = 0; i < declaredFields.length; i++)
      { // avoid to take parameter names that are equal to field names
         usedNames.add(declaredFields[i].getName());
      }

      ImportRewrite imports = getImportRewrite();
      ImportRewriteContext context = new ContextSensitiveImportRewriteContext(methodDecl, imports);
      ListRewrite listRewrite = rewrite.getListRewrite(methodDecl, MethodDeclaration.PARAMETERS_PROPERTY);

      List<SingleVariableDeclaration> parameters = methodDecl.parameters(); // old parameters
      int k = 0; // index over the oldParameters

      for (int i = 0; i < fParameterChanges.length; i++)
      {
         ChangeDescription curr = fParameterChanges[i];

         if (curr == null)
         {
            SingleVariableDeclaration oldParam = parameters.get(k);
            usedNames.add(oldParam.getName().getIdentifier());
            k++;
         }
         else if (curr instanceof InsertDescription)
         {
            InsertDescription desc = (InsertDescription)curr;
            SingleVariableDeclaration newNode = ast.newSingleVariableDeclaration();
            newNode.setType(imports.addImport(desc.type, ast, context));
            newNode.setName(ast.newSimpleName("x")); //$NON-NLS-1$

            // remember to set name later
            desc.resultingParamName = new SimpleName[]{newNode.getName()};
            desc.resultingParamType = newNode.getType();
            hasCreatedVariables = true;

            listRewrite.insertAt(newNode, i, null);

            Javadoc javadoc = methodDecl.getJavadoc();
            if (javadoc != null)
            {
               TagElement newTagElement = ast.newTagElement();
               newTagElement.setTagName(TagElement.TAG_PARAM);
               SimpleName arg = ast.newSimpleName("x"); //$NON-NLS-1$
               newTagElement.fragments().add(arg);
               insertTabStop(rewrite, newTagElement.fragments(), "param_tagcomment" + i); //$NON-NLS-1$
               insertParamTag(rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY), parameters, k, newTagElement);
               desc.resultingTagArg = arg; // set the name later
            }
            else
            {
               desc.resultingTagArg = null;
            }
         }
         else if (curr instanceof RemoveDescription)
         {
            SingleVariableDeclaration decl = parameters.get(k);

            listRewrite.remove(decl, null);
            k++;

            TagElement tagNode = findParamTag(methodDecl, decl);
            if (tagNode != null)
            {
               rewrite.remove(tagNode, null);
            }
         }
         else if (curr instanceof EditDescription)
         {
            EditDescription desc = (EditDescription)curr;

            ITypeBinding newTypeBinding = desc.type;
            SingleVariableDeclaration decl = parameters.get(k);

            if (k == parameters.size() - 1 && i == fParameterChanges.length - 1 && decl.isVarargs()
               && newTypeBinding.isArray())
            {
               newTypeBinding = newTypeBinding.getElementType(); // stick with varargs if it was before
            }
            else
            {
               rewrite.set(decl, SingleVariableDeclaration.VARARGS_PROPERTY, Boolean.FALSE, null);
            }

            Type newType = imports.addImport(newTypeBinding, ast, context);
            rewrite.replace(decl.getType(), newType, null);
            rewrite.set(decl, SingleVariableDeclaration.EXTRA_DIMENSIONS_PROPERTY, new Integer(0), null);

            IBinding binding = decl.getName().resolveBinding();
            if (binding != null)
            {
               SimpleName[] names = LinkedNodeFinder.findByBinding(decl.getRoot(), binding);
               SimpleName[] newNames = new SimpleName[names.length];
               for (int j = 0; j < names.length; j++)
               {
                  SimpleName newName = ast.newSimpleName("x"); //$NON-NLS-1$  // name will be set later
                  newNames[j] = newName;
                  rewrite.replace(names[j], newName, null);

               }
               desc.resultingParamName = newNames;
            }
            else
            {
               SimpleName newName = ast.newSimpleName("x"); //$NON-NLS-1$  // name will be set later
               rewrite.replace(decl.getName(), newName, null);
               // remember to set name later
               desc.resultingParamName = new SimpleName[]{newName};
            }

            desc.resultingParamType = newType;
            desc.orginalName = decl.getName().getIdentifier();
            hasCreatedVariables = true;

            k++;

            TagElement tagNode = findParamTag(methodDecl, decl);
            if (tagNode != null)
            {
               List<? extends ASTNode> fragments = tagNode.fragments();
               if (!fragments.isEmpty())
               {
                  SimpleName arg = ast.newSimpleName("x"); //$NON-NLS-1$
                  rewrite.replace(fragments.get(0), arg, null);
                  desc.resultingTagArg = arg;
               }
            }

         }
         else if (curr instanceof SwapDescription)
         {
            SingleVariableDeclaration decl1 = parameters.get(k);
            SingleVariableDeclaration decl2 = parameters.get(((SwapDescription)curr).index);

            rewrite.replace(decl1, rewrite.createCopyTarget(decl2), null);
            rewrite.replace(decl2, rewrite.createCopyTarget(decl1), null);

            usedNames.add(decl1.getName().getIdentifier());
            k++;

            TagElement tagNode1 = findParamTag(methodDecl, decl1);
            TagElement tagNode2 = findParamTag(methodDecl, decl2);
            if (tagNode1 != null && tagNode2 != null)
            {
               rewrite.replace(tagNode1, rewrite.createCopyTarget(tagNode2), null);
               rewrite.replace(tagNode2, rewrite.createCopyTarget(tagNode1), null);
            }
         }
      }
      if (!hasCreatedVariables)
      {
         return;
      }

      if (methodDecl.getBody() != null)
      {
         // avoid take a name of a local variable inside
         CompilationUnit root = (CompilationUnit)methodDecl.getRoot();
         IBinding[] bindings =
            (new ScopeAnalyzer(root)).getDeclarationsAfter(methodDecl.getBody().getStartPosition(),
               ScopeAnalyzer.VARIABLES);
         for (int i = 0; i < bindings.length; i++)
         {
            usedNames.add(bindings[i].getName());
         }
      }

      fixupNames(rewrite, usedNames);
   }

   private void fixupNames(ASTRewrite rewrite, ArrayList<String> usedNames)
   {
      AST ast = rewrite.getAST();
      // set names for new parameters
      for (int i = 0; i < fParameterChanges.length; i++)
      {
         ChangeDescription curr = fParameterChanges[i];
         if (curr instanceof ModifyDescription)
         {
            ModifyDescription desc = (ModifyDescription)curr;

            String typeKey = getParamTypeGroupId(i);
            String nameKey = getParamNameGroupId(i);

            // collect name suggestions
            String favourite = null;
            String[] excludedNames = usedNames.toArray(new String[usedNames.size()]);

            String suggestedName = desc.name;
            if (suggestedName != null)
            {
               favourite = StubUtility.suggestArgumentName(suggestedName, excludedNames);
               //               addLinkedPositionProposal(nameKey, favourite, null);
            }

            //            if (desc instanceof EditDescription)
            //            {
            //               addLinkedPositionProposal(nameKey, ((EditDescription)desc).orginalName, null);
            //            }

            Type type = desc.resultingParamType;
            String[] suggestedNames = StubUtility.getArgumentNameSuggestions(type, excludedNames);
            //            for (int k = 0; k < suggestedNames.length; k++)
            //            {
            //               addLinkedPositionProposal(nameKey, suggestedNames[k], null);
            //            }
            if (favourite == null)
            {
               favourite = suggestedNames[0];
            }
            usedNames.add(favourite);

            //            SimpleName[] names = desc.resultingParamName;
            //            for (int j = 0; j < names.length; j++)
            //            {
            //               names[j].setIdentifier(favourite);
            //               addLinkedPosition(rewrite.track(names[j]), false, nameKey);
            //            }

            //            addLinkedPosition(rewrite.track(desc.resultingParamType), true, typeKey);

            // collect type suggestions
            //            ITypeBinding[] bindings = ASTResolving.getRelaxingTypes(ast, desc.type);
            //            for (int k = 0; k < bindings.length; k++)
            //            {
            //               addLinkedPositionProposal(typeKey, bindings[k]);
            //            }

            //            SimpleName tagArg = desc.resultingTagArg;
            //            if (tagArg != null)
            //            {
            //               tagArg.setIdentifier(favourite);
            //               addLinkedPosition(rewrite.track(tagArg), false, nameKey);
            //            }
         }
      }
   }

   private TagElement findParamTag(MethodDeclaration decl, SingleVariableDeclaration param)
   {
      Javadoc javadoc = decl.getJavadoc();
      if (javadoc != null)
      {
         return JavadocTagsSubProcessor.findParamTag(javadoc, param.getName().getIdentifier());
      }
      return null;
   }

   private TagElement insertParamTag(ListRewrite tagRewriter, List<SingleVariableDeclaration> parameters,
      int currentIndex, TagElement newTagElement)
   {
      HashSet<String> previousNames = new HashSet<String>();
      for (int n = 0; n < currentIndex; n++)
      {
         SingleVariableDeclaration var = parameters.get(n);
         previousNames.add(var.getName().getIdentifier());
      }

      JavadocTagsSubProcessor.insertTag(tagRewriter, newTagElement, previousNames);
      return newTagElement;
   }

   private void modifyExceptions(ASTRewrite rewrite, MethodDeclaration methodDecl)
   {
      AST ast = methodDecl.getAST();

      ImportRewrite imports = getImportRewrite();
      ImportRewriteContext context = new ContextSensitiveImportRewriteContext(methodDecl, imports);
      ListRewrite listRewrite = rewrite.getListRewrite(methodDecl, MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY);

      List<Name> exceptions = methodDecl.thrownExceptions(); // old exceptions
      int k = 0; // index over the old exceptions

      for (int i = 0; i < fExceptionChanges.length; i++)
      {
         ChangeDescription curr = fExceptionChanges[i];

         if (curr == null)
         {
            k++;
         }
         else if (curr instanceof InsertDescription)
         {
            InsertDescription desc = (InsertDescription)curr;
            String type = imports.addImport(desc.type, context);
            ASTNode newNode = ASTNodeFactory.newName(ast, type);

            listRewrite.insertAt(newNode, i, null);

            String key = getExceptionTypeGroupId(i);
            //            addLinkedPosition(rewrite.track(newNode), false, key);

            Javadoc javadoc = methodDecl.getJavadoc();
            if (javadoc != null && JavadocTagsSubProcessor.findThrowsTag(javadoc, type) == null)
            {
               TagElement newTagElement = ast.newTagElement();
               newTagElement.setTagName(TagElement.TAG_THROWS);
               ASTNode newRef = ASTNodeFactory.newName(ast, type);
               newTagElement.fragments().add(newRef);
               insertTabStop(rewrite, newTagElement.fragments(), "throws_tagcomment" + i); //$NON-NLS-1$
               insertThrowsTag(rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY), exceptions, k, newTagElement);

               //               addLinkedPosition(rewrite.track(newRef), false, key);
            }

         }
         else if (curr instanceof RemoveDescription)
         {
            Name node = exceptions.get(k);

            listRewrite.remove(node, null);
            k++;

            TagElement tagNode = findThrowsTag(methodDecl, node);
            if (tagNode != null)
            {
               rewrite.remove(tagNode, null);
            }
         }
         else if (curr instanceof EditDescription)
         {
            EditDescription desc = (EditDescription)curr;

            Name oldNode = exceptions.get(k);

            String type = imports.addImport(desc.type, context);
            ASTNode newNode = ASTNodeFactory.newName(ast, type);

            listRewrite.replace(oldNode, newNode, null);
            String key = getExceptionTypeGroupId(i);
            //            addLinkedPosition(rewrite.track(newNode), false, key);

            k++;

            TagElement tagNode = findThrowsTag(methodDecl, oldNode);
            if (tagNode != null)
            {
               ASTNode newRef = ASTNodeFactory.newName(ast, type);
               rewrite.replace((ASTNode)tagNode.fragments().get(0), newRef, null);
               //               addLinkedPosition(rewrite.track(newRef), false, key);
            }

         }
         else if (curr instanceof SwapDescription)
         {
            Name decl1 = exceptions.get(k);
            Name decl2 = exceptions.get(((SwapDescription)curr).index);

            rewrite.replace(decl1, rewrite.createCopyTarget(decl2), null);
            rewrite.replace(decl2, rewrite.createCopyTarget(decl1), null);

            k++;

            TagElement tagNode1 = findThrowsTag(methodDecl, decl1);
            TagElement tagNode2 = findThrowsTag(methodDecl, decl2);
            if (tagNode1 != null && tagNode2 != null)
            {
               rewrite.replace(tagNode1, rewrite.createCopyTarget(tagNode2), null);
               rewrite.replace(tagNode2, rewrite.createCopyTarget(tagNode1), null);
            }
         }
      }
   }

   private void insertTabStop(ASTRewrite rewriter, List<ASTNode> fragments, String linkedName)
   {
      TextElement textElement = rewriter.getAST().newTextElement();
      textElement.setText(""); //$NON-NLS-1$
      fragments.add(textElement);
      //      addLinkedPosition(rewriter.track(textElement), false, linkedName);
   }

   private TagElement findThrowsTag(MethodDeclaration decl, Name exception)
   {
      Javadoc javadoc = decl.getJavadoc();
      if (javadoc != null)
      {
         String name = ASTNodes.getSimpleNameIdentifier(exception);
         return JavadocTagsSubProcessor.findThrowsTag(javadoc, name);
      }
      return null;
   }

   private TagElement insertThrowsTag(ListRewrite tagRewriter, List<Name> exceptions, int currentIndex,
      TagElement newTagElement)
   {
      HashSet<String> previousNames = new HashSet<String>();
      for (int n = 0; n < currentIndex; n++)
      {
         Name curr = exceptions.get(n);
         previousNames.add(ASTNodes.getSimpleNameIdentifier(curr));
      }

      JavadocTagsSubProcessor.insertTag(tagRewriter, newTagElement, previousNames);
      return newTagElement;
   }

   public String getParamNameGroupId(int idx)
   {
      return "param_name_" + idx; //$NON-NLS-1$
   }

   public String getParamTypeGroupId(int idx)
   {
      return "param_type_" + idx; //$NON-NLS-1$
   }

   public String getExceptionTypeGroupId(int idx)
   {
      return "exc_type_" + idx; //$NON-NLS-1$
   }

}
