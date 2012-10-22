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
package org.exoplatform.ide.java.client.internal.corext.codemanipulation;

import org.exoplatform.ide.java.client.core.Flags;
import org.exoplatform.ide.java.client.core.NamingConventions;
import org.exoplatform.ide.java.client.core.Signature;
import org.exoplatform.ide.java.client.core.dom.AST;
import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.Assignment;
import org.exoplatform.ide.java.client.core.dom.Assignment.Operator;
import org.exoplatform.ide.java.client.core.dom.CastExpression;
import org.exoplatform.ide.java.client.core.dom.Expression;
import org.exoplatform.ide.java.client.core.dom.IMethodBinding;
import org.exoplatform.ide.java.client.core.dom.ITypeBinding;
import org.exoplatform.ide.java.client.core.dom.IVariableBinding;
import org.exoplatform.ide.java.client.core.dom.InfixExpression;
import org.exoplatform.ide.java.client.core.dom.MethodDeclaration;
import org.exoplatform.ide.java.client.core.dom.NumberLiteral;
import org.exoplatform.ide.java.client.core.dom.ParenthesizedExpression;
import org.exoplatform.ide.java.client.core.dom.PostfixExpression;
import org.exoplatform.ide.java.client.core.dom.PrefixExpression;
import org.exoplatform.ide.java.client.core.dom.PrimitiveType;
import org.exoplatform.ide.java.client.core.dom.SingleVariableDeclaration;
import org.exoplatform.ide.java.client.core.dom.TypeDeclaration;
import org.exoplatform.ide.java.client.core.dom.rewrite.ASTRewrite;
import org.exoplatform.ide.java.client.internal.corext.dom.ASTNodes;
import org.exoplatform.ide.java.client.internal.corext.dom.NecessaryParenthesesChecker;
import org.exoplatform.ide.java.client.internal.corext.util.JdtFlags;
import org.exoplatform.ide.runtime.CoreException;

import java.util.List;

public class GetterSetterUtil
{

   private static final String[] EMPTY = new String[0];

   //no instances
   private GetterSetterUtil()
   {
   }

   public static String getGetterName(IVariableBinding field, String[] excludedNames)
   {
      boolean useIs = StubUtility.useIsForBooleanGetters();
      return getGetterName(field, excludedNames, useIs);
   }

   //
   //   private static String getGetterName(IField field, String[] excludedNames, boolean useIsForBoolGetters)
   //   {
   //      if (excludedNames == null)
   //      {
   //         excludedNames = EMPTY;
   //      }
   //      return getGetterName(field.getJavaProject(), field.getElementName(), field.getFlags(), useIsForBoolGetters
   //         && JavaModelUtil.isBoolean(field), excludedNames);
   //   }

   public static String getGetterName(IVariableBinding variableType, String[] excludedNames, boolean isBoolean)
   {
      boolean useIs = isBoolean(variableType) && isBoolean;
      return getGetterName(variableType.getName(), variableType.getModifiers(), useIs, excludedNames);
   }

   public static String getGetterName(String fieldName, int flags, boolean isBoolean, String[] excludedNames)
   {
      return NamingConventions.suggestGetterName(fieldName, flags, isBoolean, excludedNames);
   }

   public static String getSetterName(IVariableBinding variableType, String[] excludedNames, boolean isBoolean)
   {
      return getSetterName(variableType.getName(), variableType.getModifiers(), isBoolean, excludedNames);
   }

   public static String getSetterName(String fieldName, int flags, boolean isBoolean, String[] excludedNames)
   {
      boolean useIs = StubUtility.useIsForBooleanGetters();
      return NamingConventions.suggestSetterName(fieldName, flags, useIs && isBoolean, excludedNames);
   }

   public static String getSetterName(IVariableBinding field, String[] excludedNames)
   {
      if (excludedNames == null)
      {
         excludedNames = EMPTY;
      }
      return getSetterName(field.getName(), field.getModifiers(), isBoolean(field), excludedNames);
   }

   /**
    * Checks if the field is boolean.
    * @param field the field
    * @return returns <code>true</code> if the field returns a boolean
    * @throws JavaModelException thrown when the field can not be accessed
    */
   public static boolean isBoolean(IVariableBinding field)
   {
      return field.getType().getBinaryName().equals(Signature.SIG_BOOLEAN);
   }

   public static IMethodBinding getGetter(IVariableBinding field)
   {
      String getterName = getGetterName(field, EMPTY, true);
      IMethodBinding primaryCandidate = findMethod(getterName, new String[0], false, field.getDeclaringClass());
      if (!isBoolean(field) || (primaryCandidate != null))
         return primaryCandidate;
      //bug 30906 describes why we need to look for other alternatives here (try with get... for booleans)
      String secondCandidateName = getGetterName(field, EMPTY, false);
      return findMethod(secondCandidateName, new String[0], false, field.getDeclaringClass());
   }

   public static IMethodBinding getSetter(IVariableBinding field)
   {
      String[] args = new String[]{field.getType().getKey().replaceAll("/", ".")};
      return findMethod(getSetterName(field, EMPTY), args, false, field.getDeclaringClass());
   }

   /**
    * Finds a method in a type.
    * This searches for a method with the same name and signature. Parameter types are only
    * compared by the simple name, no resolving for the fully qualified type name is done.
    * Constructors are only compared by parameters, not the name.
    * @param name The name of the method to find
    * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
    * @param isConstructor If the method is a constructor
    * @param type the type
    * @return The first found method or <code>null</code>, if nothing foun
    * @throws JavaModelException thrown when the type can not be accessed
    */
   public static IMethodBinding findMethod(String name, String[] paramTypes, boolean isConstructor, ITypeBinding type)
   {
      IMethodBinding[] methods = type.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (isSameMethodSignature(name, paramTypes, isConstructor, methods[i]))
         {
            return methods[i];
         }
      }
      return null;
   }
   /**
    * Finds a method in a type.
    * This searches for a method with the same name and signature. Parameter types are only
    * compared by the simple name, no resolving for the fully qualified type name is done.
    * Constructors are only compared by parameters, not the name.
    * @param name The name of the method to find
    * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
    * @param isConstructor If the method is a constructor
    * @param type the type
    * @return The first found method or <code>null</code>, if nothing foun
    * @throws JavaModelException thrown when the type can not be accessed
    */
   public static MethodDeclaration findMethod(String name, String[] paramTypes, boolean isConstructor, TypeDeclaration type)
   {
      MethodDeclaration[] methods = type.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (isSameMethodSignature(name, paramTypes, isConstructor, methods[i]))
         {
            return methods[i];
         }
      }
      return null;
   }

   /**
    * Tests if a method equals to the given signature.
    * Parameter types are only compared by the simple name, no resolving for
    * the fully qualified type name is done. Constructors are only compared by
    * parameters, not the name.
    * @param name Name of the method
    * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
    * @param isConstructor Specifies if the method is a constructor
    * @param curr the method
    * @return Returns <code>true</code> if the method has the given name and parameter types and constructor state.
    * @throws JavaModelException thrown when the method can not be accessed
    */
   public static boolean isSameMethodSignature(String name, String[] paramTypes, boolean isConstructor,
      IMethodBinding curr)
   {
      if (isConstructor || name.equals(curr.getName()))
      {
         if (isConstructor == curr.isConstructor())
         {
            ITypeBinding[] currParamTypes = curr.getParameterTypes();
            if (paramTypes.length == currParamTypes.length)
            {
               for (int i = 0; i < paramTypes.length; i++)
               {
                  String t1 = Signature.getSimpleName(Signature.toString(paramTypes[i]));
                  String t2 = Signature.getSimpleName(currParamTypes[i].getQualifiedName());
                  if (!t1.equals(t2))
                  {
                     return false;
                  }
               }
               return true;
            }
         }
      }
      return false;
   }
   
   /**
    * Tests if a method equals to the given signature.
    * Parameter types are only compared by the simple name, no resolving for
    * the fully qualified type name is done. Constructors are only compared by
    * parameters, not the name.
    * @param name Name of the method
    * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
    * @param isConstructor Specifies if the method is a constructor
    * @param curr the method
    * @return Returns <code>true</code> if the method has the given name and parameter types and constructor state.
    * @throws JavaModelException thrown when the method can not be accessed
    */
   public static boolean isSameMethodSignature(String name, String[] paramTypes, boolean isConstructor,
      MethodDeclaration curr)
   {
      if (isConstructor || name.equals(curr.getName()))
      {
         if (isConstructor == curr.isConstructor())
         {
            List<SingleVariableDeclaration> currParamTypes = curr.parameters();
            if (paramTypes.length == currParamTypes.size())
            {
               for (int i = 0; i < paramTypes.length; i++)
               {
                  String t1 = Signature.getSimpleName(Signature.toString(paramTypes[i]));
                  String t2 = Signature.getSimpleName(currParamTypes.get(i).getName().getFullyQualifiedName());
                  if (!t1.equals(t2))
                  {
                     return false;
                  }
               }
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Create a stub for a getter of the given field using getter/setter templates. The resulting code
    * has to be formatted and indented.
    * @param field The field to create a getter for
    * @param setterName The chosen name for the setter
    * @param addComments If <code>true</code>, comments will be added.
    * @param flags The flags signaling visibility, if static, synchronized or final
    * @return Returns the generated stub.
    * @throws CoreException when stub creation failed
    */
   public static String getSetterStub(IVariableBinding field, String setterName, boolean addComments, int flags)
      throws CoreException
   {

      String fieldName = field.getName();
      ITypeBinding parentType = field.getDeclaringClass();

      String returnSig = field.getType().getKey().replaceAll("/", ".");
      String typeName = Signature.getSimpleName(Signature.toString(returnSig));

      String accessorName = StubUtility.getBaseName(field);
      String argname = StubUtility.suggestArgumentName(accessorName, EMPTY);

      boolean isStatic = Flags.isStatic(flags);
      boolean isSync = Flags.isSynchronized(flags);
      boolean isFinal = Flags.isFinal(flags);

      String lineDelim = "\n"; // Use default line delimiter, as generated stub has to be formatted anyway //$NON-NLS-1$
      StringBuffer buf = new StringBuffer();
      if (addComments)
      {
         String comment =
            StubUtility.getSetterComment(parentType.getQualifiedName(), setterName, field.getName(), typeName, argname,
               accessorName, lineDelim);
         if (comment != null)
         {
            buf.append(comment);
            buf.append(lineDelim);
         }
      }
      buf.append(JdtFlags.getVisibilityString(flags));
      buf.append(' ');
      if (isStatic)
         buf.append("static "); //$NON-NLS-1$
      if (isSync)
         buf.append("synchronized "); //$NON-NLS-1$
      if (isFinal)
         buf.append("final "); //$NON-NLS-1$

      buf.append("void "); //$NON-NLS-1$
      buf.append(setterName);
      buf.append('(');
      buf.append(typeName);
      buf.append(' ');
      buf.append(argname);
      buf.append(") {"); //$NON-NLS-1$
      buf.append(lineDelim);

      boolean useThis = StubUtility.useThisForFieldAccess();
      if (argname.equals(fieldName) || (useThis && !isStatic))
      {
         if (isStatic)
            fieldName = parentType.getName() + '.' + fieldName;
         else
            fieldName = "this." + fieldName; //$NON-NLS-1$
      }
      String body =
         StubUtility.getSetterMethodBodyContent(parentType.getQualifiedName(), setterName, fieldName, argname,
            lineDelim);
      if (body != null)
      {
         buf.append(body);
      }
      buf.append("}"); //$NON-NLS-1$
      buf.append(lineDelim);
      return buf.toString();
   }

   /**
    * Create a stub for a getter of the given field using getter/setter templates. The resulting code
    * has to be formatted and indented.
    * @param field The field to create a getter for
    * @param getterName The chosen name for the getter
    * @param addComments If <code>true</code>, comments will be added.
    * @param flags The flags signaling visibility, if static, synchronized or final
    * @return Returns the generated stub.
    * @throws CoreException when stub creation failed
    */
   public static String getGetterStub(IVariableBinding field, String getterName, boolean addComments, int flags)
      throws CoreException
   {
      String fieldName = field.getName();
      ITypeBinding parentType = field.getDeclaringClass();

      boolean isStatic = Flags.isStatic(flags);
      boolean isSync = Flags.isSynchronized(flags);
      boolean isFinal = Flags.isFinal(flags);

      String typeName = Signature.getSimpleName(Signature.toString(field.getType().getKey().replaceAll("/", ".")));
      String accessorName = StubUtility.getBaseName(field);

      String lineDelim = "\n"; // Use default line delimiter, as generated stub has to be formatted anyway //$NON-NLS-1$
      StringBuffer buf = new StringBuffer();
      if (addComments)
      {
         String comment =
            StubUtility.getGetterComment(parentType.getQualifiedName(), getterName, field.getName(), typeName,
               accessorName, lineDelim);
         if (comment != null)
         {
            buf.append(comment);
            buf.append(lineDelim);
         }
      }

      buf.append(JdtFlags.getVisibilityString(flags));
      buf.append(' ');
      if (isStatic)
         buf.append("static "); //$NON-NLS-1$
      if (isSync)
         buf.append("synchronized "); //$NON-NLS-1$
      if (isFinal)
         buf.append("final "); //$NON-NLS-1$

      buf.append(typeName);
      buf.append(' ');
      buf.append(getterName);
      buf.append("() {"); //$NON-NLS-1$
      buf.append(lineDelim);

      boolean useThis = StubUtility.useThisForFieldAccess();
      if (useThis && !isStatic)
      {
         fieldName = "this." + fieldName; //$NON-NLS-1$
      }

      String body =
         StubUtility.getGetterMethodBodyContent(parentType.getQualifiedName(), getterName, fieldName, lineDelim);
      if (body != null)
      {
         buf.append(body);
      }
      buf.append("}"); //$NON-NLS-1$
      buf.append(lineDelim);
      return buf.toString();
   }

   /**
    * Converts an assignment, postfix expression or prefix expression into an assignable equivalent expression using the getter.
    *
    * @param node the assignment/prefix/postfix node
    * @param astRewrite the astRewrite to use
    * @param getterExpression the expression to insert for read accesses or <code>null</code> if such an expression does not exist
    * @param variableType the type of the variable that the result will be assigned to
    * @param is50OrHigher <code>true</code> if a 5.0 or higher environment can be used
    * @return an expression that can be assigned to the type variableType with node being replaced by a equivalent expression using the getter
    */
   public static Expression getAssignedValue(ASTNode node, ASTRewrite astRewrite, Expression getterExpression,
      ITypeBinding variableType, boolean is50OrHigher)
   {
      InfixExpression.Operator op = null;
      AST ast = astRewrite.getAST();
      if (isNotInBlock(node))
         return null;
      if (node.getNodeType() == ASTNode.ASSIGNMENT)
      {
         Assignment assignment = ((Assignment)node);
         Expression rightHandSide = assignment.getRightHandSide();
         Expression copiedRightOp = (Expression)astRewrite.createCopyTarget(rightHandSide);
         if (assignment.getOperator() == Operator.ASSIGN)
         {
            ITypeBinding rightHandSideType = rightHandSide.resolveTypeBinding();
            copiedRightOp =
               createNarrowCastIfNessecary(copiedRightOp, rightHandSideType, ast, variableType, is50OrHigher);
            return copiedRightOp;
         }
         if (getterExpression != null)
         {
            InfixExpression infix = ast.newInfixExpression();
            infix.setLeftOperand(getterExpression);
            infix.setOperator(ASTNodes.convertToInfixOperator(assignment.getOperator()));
            ITypeBinding infixType = infix.resolveTypeBinding();
            if (NecessaryParenthesesChecker.needsParentheses(copiedRightOp, infix,
               InfixExpression.RIGHT_OPERAND_PROPERTY))
            {
               //TODO: this introduces extra parentheses as the new "infix" node doesn't have bindings
               ParenthesizedExpression p = ast.newParenthesizedExpression();
               p.setExpression(copiedRightOp);
               copiedRightOp = p;
            }
            infix.setRightOperand(copiedRightOp);
            return createNarrowCastIfNessecary(infix, infixType, ast, variableType, is50OrHigher);
         }
      }
      else if (node.getNodeType() == ASTNode.POSTFIX_EXPRESSION)
      {
         PostfixExpression po = (PostfixExpression)node;
         if (po.getOperator() == PostfixExpression.Operator.INCREMENT)
            op = InfixExpression.Operator.PLUS;
         if (po.getOperator() == PostfixExpression.Operator.DECREMENT)
            op = InfixExpression.Operator.MINUS;
      }
      else if (node.getNodeType() == ASTNode.PREFIX_EXPRESSION)
      {
         PrefixExpression pe = (PrefixExpression)node;
         if (pe.getOperator() == PrefixExpression.Operator.INCREMENT)
            op = InfixExpression.Operator.PLUS;
         if (pe.getOperator() == PrefixExpression.Operator.DECREMENT)
            op = InfixExpression.Operator.MINUS;
      }
      if (op != null && getterExpression != null)
      {
         return createInfixInvocationFromPostPrefixExpression(op, getterExpression, ast, variableType, is50OrHigher);
      }
      return null;
   }

   /*
    * Check if the node is in a block. We don't want to update declarations
    */
   private static boolean isNotInBlock(ASTNode parent)
   {
      ASTNode statement = parent.getParent();
      boolean isStatement = statement.getNodeType() != ASTNode.EXPRESSION_STATEMENT;
      ASTNode block = statement.getParent();
      boolean isBlock = block.getNodeType() == ASTNode.BLOCK || block.getNodeType() == ASTNode.SWITCH_STATEMENT;
      boolean isControlStatemenBody = ASTNodes.isControlStatementBody(statement.getLocationInParent());
      return isStatement || !(isBlock || isControlStatemenBody);
   }

   private static Expression createInfixInvocationFromPostPrefixExpression(InfixExpression.Operator operator,
      Expression getterExpression, AST ast, ITypeBinding variableType, boolean is50OrHigher)
   {
      InfixExpression infix = ast.newInfixExpression();
      infix.setLeftOperand(getterExpression);
      infix.setOperator(operator);
      NumberLiteral number = ast.newNumberLiteral();
      number.setToken("1"); //$NON-NLS-1$
      infix.setRightOperand(number);
      ITypeBinding infixType = infix.resolveTypeBinding();
      return createNarrowCastIfNessecary(infix, infixType, ast, variableType, is50OrHigher);
   }

   /**
    * Checks if the assignment needs a downcast and inserts it if necessary
    *
    * @param expression the right hand-side
    * @param expressionType the type of the right hand-side. Can be null
    * @param ast the AST
    * @param variableType the Type of the variable the expression will be assigned to
    * @param is50OrHigher if <code>true</code> java 5.0 code will be assumed
    * @return the casted expression if necessary
    */
   private static Expression createNarrowCastIfNessecary(Expression expression, ITypeBinding expressionType, AST ast,
      ITypeBinding variableType, boolean is50OrHigher)
   {
      PrimitiveType castTo = null;
      if (variableType.isEqualTo(expressionType))
         return expression; //no cast for same type
      if (is50OrHigher)
      {
         if (ast.resolveWellKnownType("java.lang.Character").isEqualTo(variableType)) //$NON-NLS-1$
            castTo = ast.newPrimitiveType(PrimitiveType.CHAR);
         if (ast.resolveWellKnownType("java.lang.Byte").isEqualTo(variableType)) //$NON-NLS-1$
            castTo = ast.newPrimitiveType(PrimitiveType.BYTE);
         if (ast.resolveWellKnownType("java.lang.Short").isEqualTo(variableType)) //$NON-NLS-1$
            castTo = ast.newPrimitiveType(PrimitiveType.SHORT);
      }
      if (ast.resolveWellKnownType("char").isEqualTo(variableType)) //$NON-NLS-1$
         castTo = ast.newPrimitiveType(PrimitiveType.CHAR);
      if (ast.resolveWellKnownType("byte").isEqualTo(variableType)) //$NON-NLS-1$
         castTo = ast.newPrimitiveType(PrimitiveType.BYTE);
      if (ast.resolveWellKnownType("short").isEqualTo(variableType)) //$NON-NLS-1$
         castTo = ast.newPrimitiveType(PrimitiveType.SHORT);
      if (castTo != null)
      {
         CastExpression cast = ast.newCastExpression();
         if (NecessaryParenthesesChecker.needsParentheses(expression, cast, CastExpression.EXPRESSION_PROPERTY))
         {
            ParenthesizedExpression parenthesized = ast.newParenthesizedExpression();
            parenthesized.setExpression(expression);
            cast.setExpression(parenthesized);
         }
         else
            cast.setExpression(expression);
         cast.setType(castTo);
         return cast;
      }
      return expression;
   }

}
