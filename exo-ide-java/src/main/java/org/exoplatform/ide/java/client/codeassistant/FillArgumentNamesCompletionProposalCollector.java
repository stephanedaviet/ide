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
package org.exoplatform.ide.java.client.codeassistant;

import org.exoplatform.ide.java.client.codeassistant.api.JavaCompletionProposal;
import org.exoplatform.ide.java.client.core.CompletionProposal;
import org.exoplatform.ide.java.client.core.Signature;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.text.Document;

/**
 * Completion proposal collector which creates proposals with filled in argument names.
 * <p>
 * This collector is used when {@link PreferenceConstants#CODEASSIST_FILL_ARGUMENT_NAMES} is enabled.
 * <p/>
 */
public final class FillArgumentNamesCompletionProposalCollector extends CompletionProposalCollector
{

   private final boolean fIsGuessArguments;

   public FillArgumentNamesCompletionProposalCollector(CompilationUnit unit, Document document, int invocationOffset,
      String projectId, String docContext)
   {
      super(unit, false, document, invocationOffset, projectId, docContext);
      fIsGuessArguments = true;// preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_GUESS_METHOD_ARGUMENTS);
      setRequireExtendedContext(true);
   }

   /**
    * @param invocationContext
    */
   public FillArgumentNamesCompletionProposalCollector(JavaContentAssistInvocationContext invocationContext)
   {
      this(invocationContext.getCompilationUnit(), invocationContext.getDocument(), invocationContext
         .getInvocationOffset(), invocationContext.getProjectId(), invocationContext.getDocContext());
      setInvocationContext(invocationContext);
   }

   /*
    * @see
    * org.eclipse.jdt.internal.ui.text.java.ResultCollector#createJavaCompletionProposal(org.eclipse.jdt.core.CompletionProposal)
    */
   @Override
   protected JavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal)
   {
      switch (proposal.getKind())
      {
         case CompletionProposal.METHOD_REF :
         case CompletionProposal.CONSTRUCTOR_INVOCATION :
         case CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER :
            return createMethodReferenceProposal(proposal);
         case CompletionProposal.TYPE_REF :
            return createTypeProposal(proposal);
         default :
            return super.createJavaCompletionProposal(proposal);
      }
   }

   private JavaCompletionProposal createMethodReferenceProposal(CompletionProposal methodProposal)
   {
      String completion = String.valueOf(methodProposal.getCompletion());
      // super class' behavior if this is not a normal completion or has no
      // parameters
      if ((completion.length() == 0) || ((completion.length() == 1) && completion.charAt(0) == ')')
         || Signature.getParameterCount(methodProposal.getSignature()) == 0 || getContext().isInJavadoc())
         return super.createJavaCompletionProposal(methodProposal);

      LazyJavaCompletionProposal proposal = null;
      proposal = ParameterGuessingProposal.createProposal(methodProposal, getInvocationContext(), fIsGuessArguments);
      if (proposal == null)
      {
         proposal = new FilledArgumentNamesMethodProposal(methodProposal, getInvocationContext());
      }
      return proposal;
   }

   /*
    * @see org.eclipse.jdt.internal.ui.text.java.ResultCollector#createTypeCompletion(org.eclipse.jdt.core.CompletionProposal)
    */
   JavaCompletionProposal createTypeProposal(CompletionProposal typeProposal)
   {
      // final ICompilationUnit cu= getCompilationUnit();
      if (getContext() != null && getContext().isInJavadoc())
         return super.createJavaCompletionProposal(typeProposal);

      // IJavaProject project= cu.getJavaProject();
      if (!shouldProposeGenerics())
         return super.createJavaCompletionProposal(typeProposal);

      char[] completion = typeProposal.getCompletion();
      // don't add parameters for import-completions nor for proposals with an empty completion (e.g. inside the type argument
      // list)
      if (completion.length > 0
         && (completion[completion.length - 1] == ';' || completion[completion.length - 1] == '.'))
         return super.createJavaCompletionProposal(typeProposal);

      LazyJavaCompletionProposal newProposal = new LazyGenericTypeProposal(typeProposal, getInvocationContext());
      return newProposal;
   }

   /**
    * Returns <code>true</code> if generic proposals should be allowed, <code>false</code> if not. Note that even though code (in
    * a library) may be referenced that uses generics, it is still possible that the current source does not allow generics.
    * 
    * @return <code>true</code> if the generic proposals should be allowed, <code>false</code> if not
    */
   private final boolean shouldProposeGenerics()
   {
      // String sourceVersion;
      //
      // sourceVersion= JavaCore.getOption(JavaCore.COMPILER_SOURCE);
      // TODO
      return true;
   }
}
