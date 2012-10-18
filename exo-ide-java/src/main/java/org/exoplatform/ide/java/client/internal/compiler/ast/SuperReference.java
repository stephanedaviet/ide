/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.java.client.internal.compiler.ast;

import org.exoplatform.ide.java.client.internal.compiler.ASTVisitor;
import org.exoplatform.ide.java.client.internal.compiler.flow.FlowContext;
import org.exoplatform.ide.java.client.internal.compiler.flow.FlowInfo;
import org.exoplatform.ide.java.client.internal.compiler.impl.Constant;
import org.exoplatform.ide.java.client.internal.compiler.lookup.BlockScope;
import org.exoplatform.ide.java.client.internal.compiler.lookup.ReferenceBinding;
import org.exoplatform.ide.java.client.internal.compiler.lookup.TypeBinding;

public class SuperReference extends ThisReference
{

   public SuperReference(int sourceStart, int sourceEnd)
   {

      super(sourceStart, sourceEnd);
   }

   public static ExplicitConstructorCall implicitSuperConstructorCall()
   {

      return new ExplicitConstructorCall(ExplicitConstructorCall.ImplicitSuper);
   }

   public boolean isImplicitThis()
   {

      return false;
   }

   public boolean isSuper()
   {

      return true;
   }

   public boolean isThis()
   {

      return false;
   }

   public StringBuffer printExpression(int indent, StringBuffer output)
   {

      return output.append("super"); //$NON-NLS-1$

   }

   public TypeBinding resolveType(BlockScope scope)
   {

      this.constant = Constant.NotAConstant;
      if (!checkAccess(scope.methodScope()))
         return null;
      ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
      if (enclosingReceiverType.id == T_JavaLangObject)
      {
         scope.problemReporter().cannotUseSuperInJavaLangObject(this);
         return null;
      }
      return this.resolvedType = enclosingReceiverType.superclass();
   }

   public void traverse(ASTVisitor visitor, BlockScope blockScope)
   {
      visitor.visit(this, blockScope);
      visitor.endVisit(this, blockScope);
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
      boolean valueRequired)
   {
      currentScope.resetEnclosingMethodStaticFlag();
      return analyseCode(currentScope, flowContext, flowInfo);
   }
}
