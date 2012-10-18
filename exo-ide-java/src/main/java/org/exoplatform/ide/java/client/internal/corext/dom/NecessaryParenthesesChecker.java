/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.exoplatform.ide.java.client.internal.corext.dom;

import java.util.Iterator;

import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.ArrayAccess;
import org.exoplatform.ide.java.client.core.dom.AssertStatement;
import org.exoplatform.ide.java.client.core.dom.ChildListPropertyDescriptor;
import org.exoplatform.ide.java.client.core.dom.ConditionalExpression;
import org.exoplatform.ide.java.client.core.dom.DoStatement;
import org.exoplatform.ide.java.client.core.dom.EnhancedForStatement;
import org.exoplatform.ide.java.client.core.dom.Expression;
import org.exoplatform.ide.java.client.core.dom.ForStatement;
import org.exoplatform.ide.java.client.core.dom.ITypeBinding;
import org.exoplatform.ide.java.client.core.dom.IfStatement;
import org.exoplatform.ide.java.client.core.dom.InfixExpression;
import org.exoplatform.ide.java.client.core.dom.InfixExpression.Operator;
import org.exoplatform.ide.java.client.core.dom.ParenthesizedExpression;
import org.exoplatform.ide.java.client.core.dom.ReturnStatement;
import org.exoplatform.ide.java.client.core.dom.SingleVariableDeclaration;
import org.exoplatform.ide.java.client.core.dom.StructuralPropertyDescriptor;
import org.exoplatform.ide.java.client.core.dom.SwitchCase;
import org.exoplatform.ide.java.client.core.dom.SwitchStatement;
import org.exoplatform.ide.java.client.core.dom.SynchronizedStatement;
import org.exoplatform.ide.java.client.core.dom.ThrowStatement;
import org.exoplatform.ide.java.client.core.dom.VariableDeclarationFragment;
import org.exoplatform.ide.java.client.core.dom.WhileStatement;
import org.exoplatform.ide.java.client.internal.corext.refactoring.code.OperatorPrecedence;

/**
 * Helper class to check if an expression requires parentheses.
 * 
 * @since 3.7
 */
public class NecessaryParenthesesChecker
{

   /*
    * Get the expression wrapped by the parentheses
    * i.e. ((((expression)))) -> expression
    */
   private static Expression getExpression(ParenthesizedExpression node)
   {
      Expression expression = node.getExpression();
      while (expression instanceof ParenthesizedExpression)
      {
         expression = ((ParenthesizedExpression)expression).getExpression();
      }
      return expression;
   }

   private static boolean expressionTypeNeedsParentheses(Expression expression)
   {
      int type = expression.getNodeType();
      return type == ASTNode.INFIX_EXPRESSION || type == ASTNode.CONDITIONAL_EXPRESSION
         || type == ASTNode.PREFIX_EXPRESSION || type == ASTNode.POSTFIX_EXPRESSION || type == ASTNode.CAST_EXPRESSION
         || type == ASTNode.INSTANCEOF_EXPRESSION || type == ASTNode.ARRAY_CREATION || type == ASTNode.ASSIGNMENT;
   }

   private static boolean locationNeedsParentheses(StructuralPropertyDescriptor locationInParent)
   {
      if (locationInParent instanceof ChildListPropertyDescriptor
         && locationInParent != InfixExpression.EXTENDED_OPERANDS_PROPERTY)
      {
         // e.g. argument lists of MethodInvocation, ClassInstanceCreation, dimensions of ArrayCreation ...
         return false;
      }
      if (locationInParent == VariableDeclarationFragment.INITIALIZER_PROPERTY
         || locationInParent == SingleVariableDeclaration.INITIALIZER_PROPERTY
         || locationInParent == ReturnStatement.EXPRESSION_PROPERTY
         || locationInParent == EnhancedForStatement.EXPRESSION_PROPERTY
         || locationInParent == ForStatement.EXPRESSION_PROPERTY
         || locationInParent == WhileStatement.EXPRESSION_PROPERTY
         || locationInParent == DoStatement.EXPRESSION_PROPERTY
         || locationInParent == AssertStatement.EXPRESSION_PROPERTY
         || locationInParent == AssertStatement.MESSAGE_PROPERTY || locationInParent == IfStatement.EXPRESSION_PROPERTY
         || locationInParent == SwitchStatement.EXPRESSION_PROPERTY
         || locationInParent == SwitchCase.EXPRESSION_PROPERTY || locationInParent == ArrayAccess.INDEX_PROPERTY
         || locationInParent == ThrowStatement.EXPRESSION_PROPERTY
         || locationInParent == SynchronizedStatement.EXPRESSION_PROPERTY
         || locationInParent == ParenthesizedExpression.EXPRESSION_PROPERTY)
      {
         return false;
      }
      return true;
   }

   /*
    * Do all operands in expression have same type
    */
   private static boolean isAllOperandsHaveSameType(InfixExpression expression)
   {
      ITypeBinding binding = expression.getLeftOperand().resolveTypeBinding();
      if (binding == null)
         return false;

      ITypeBinding current = expression.getRightOperand().resolveTypeBinding();
      if (binding != current)
         return false;

      for (Iterator<Expression> iterator = expression.extendedOperands().iterator(); iterator.hasNext();)
      {
         Expression operand = iterator.next();
         current = operand.resolveTypeBinding();
         if (binding != current)
            return false;
      }

      return true;
   }

   /*
    * Is the expression of integer type
    */
   private static boolean isExpressionIntegerType(Expression expression)
   {
      ITypeBinding binding = expression.resolveTypeBinding();
      if (binding == null)
         return false;

      if (!binding.isPrimitive())
         return false;

      String name = binding.getName();
      if (isIntegerNumber(name))
         return true;

      return false;
   }

   private static boolean isExpressionStringType(Expression expression)
   {
      ITypeBinding binding = expression.resolveTypeBinding();
      if (binding == null)
         return false;

      return "java.lang.String".equals(binding.getQualifiedName()); //$NON-NLS-1$
   }

   /*
    * Is the given expression associative?
    * 
    * This is true if and only if:<br>
    * <code>left operator (right) == (right) operator left == right operator left</code>
    */
   private static boolean isAssociative(InfixExpression expression)
   {
      Operator operator = expression.getOperator();

      if (operator == InfixExpression.Operator.PLUS)
         return isExpressionStringType(expression) || isExpressionIntegerType(expression)
            && isAllOperandsHaveSameType(expression);

      if (operator == InfixExpression.Operator.TIMES)
         return isExpressionIntegerType(expression) && isAllOperandsHaveSameType(expression);

      if (operator == InfixExpression.Operator.CONDITIONAL_AND || operator == InfixExpression.Operator.CONDITIONAL_OR
         || operator == InfixExpression.Operator.AND || operator == InfixExpression.Operator.OR
         || operator == InfixExpression.Operator.XOR)
         return true;

      return false;
   }

   private static boolean isIntegerNumber(String name)
   {
      return "int".equals(name) || "long".equals(name) || "byte".equals(name) || "char".equals(name) || "short".equals(name); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
   }

   private static boolean needsParenthesesInInfixExpression(Expression expression, InfixExpression parentInfix,
      StructuralPropertyDescriptor locationInParent)
   {
      if (locationInParent == InfixExpression.LEFT_OPERAND_PROPERTY)
      {
         //we have (expr op expr) op expr
         //infix expressions are evaluated from left to right -> parentheses not needed
         return false;
      }
      else if (isAssociative(parentInfix))
      {
         //we have parent op (expr op expr) and op is associative
         //left op (right) == (right) op left == right op left
         if (expression instanceof InfixExpression)
         {
            InfixExpression infixExpression = (InfixExpression)expression;
            Operator operator = infixExpression.getOperator();

            if (isExpressionStringType(parentInfix))
            {
               if (parentInfix.getOperator() == InfixExpression.Operator.PLUS
                  && operator == InfixExpression.Operator.PLUS && isExpressionStringType(infixExpression))
               {
                  // 1 + ("" + 2) == 1 + "" + 2
                  // 1 + (2 + "") != 1 + 2 + ""
                  // "" + (2 + "") == "" + 2 + ""
                  return !isExpressionStringType(infixExpression.getLeftOperand())
                     && !isExpressionStringType(parentInfix.getLeftOperand());
               }
               //"" + (1 + 2), "" + (1 - 2) etc
               return true;
            }

            if (parentInfix.getOperator() != InfixExpression.Operator.TIMES)
               return false;

            if (operator == InfixExpression.Operator.TIMES)
               // x * (y * z) == x * y * z
               return false;

            if (operator == InfixExpression.Operator.REMAINDER || operator == InfixExpression.Operator.DIVIDE)
               // x * (y % z) != x * y % z , x * (y / z) == x * y / z rounding involved
               return true;

            return false;
         }
         return false;
      }
      else
      {
         return true;
      }
   }

   /**
    * Can the parentheses be removed from the parenthesized expression ?
    * 
    * <p>
    * <b>Note:</b> The parenthesized expression must not be an unparented node.
    * </p>
    * 
    * @param expression the parenthesized expression
    * @return <code>true</code> if parentheses can be removed, <code>false</code> otherwise.
    */
   public static boolean canRemoveParentheses(Expression expression)
   {
      return canRemoveParentheses(expression, expression.getParent(), expression.getLocationInParent());
   }

   /**
    * Can the parentheses be removed from the parenthesized expression when inserted into
    * <code>parent</code> at <code>locationInParent</code> ?
    * 
    * <p>
    * <b>Note:</b> The parenthesized expression can be an unparented node.
    * </p>
    * 
    * @param expression the parenthesized expression
    * @param parent the parent node
    * @param locationInParent location of expression in the parent
    * @return <code>true</code> if parentheses can be removed, <code>false</code> otherwise.
    */
   public static boolean canRemoveParentheses(Expression expression, ASTNode parent,
      StructuralPropertyDescriptor locationInParent)
   {
      if (!(expression instanceof ParenthesizedExpression))
      {
         return false;
      }
      return !needsParentheses(getExpression((ParenthesizedExpression)expression), parent, locationInParent);
   }

   /**
    * Does the <code>expression</code> need parentheses when inserted into <code>parent</code> at
    * <code>locationInParent</code> ?
    * 
    * <p>
    * <b>Note:</b> The expression can be an unparented node.
    * </p>
    * 
    * @param expression the expression
    * @param parent the parent node
    * @param locationInParent location of expression in the parent
    * @return <code>true</code> if the expression needs parentheses, <code>false</code> otherwise.
    */
   public static boolean needsParentheses(Expression expression, ASTNode parent,
      StructuralPropertyDescriptor locationInParent)
   {
      if (!expressionTypeNeedsParentheses(expression))
         return false;

      if (!locationNeedsParentheses(locationInParent))
      {
         return false;
      }

      if (parent instanceof Expression)
      {
         Expression parentExpression = (Expression)parent;

         int expressionPrecedence = OperatorPrecedence.getExpressionPrecedence(expression);
         int parentPrecedence = OperatorPrecedence.getExpressionPrecedence(parentExpression);

         if (expressionPrecedence > parentPrecedence)
            //(opEx) opParent and opEx binds more -> parentheses not needed
            return false;

         if (expressionPrecedence < parentPrecedence)
            //(opEx) opParent and opEx binds less -> parentheses needed
            return true;

         //(opEx) opParent binds equal

         if (parentExpression instanceof InfixExpression)
         {
            return needsParenthesesInInfixExpression(expression, (InfixExpression)parentExpression, locationInParent);
         }

         if (parentExpression instanceof ConditionalExpression
            && locationInParent == ConditionalExpression.EXPRESSION_PROPERTY)
         {
            return true;
         }

         return false;
      }

      return true;
   }
}