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

import org.exoplatform.ide.java.client.codeassistant.QualifiedTypeNameHistory;
import org.exoplatform.ide.java.client.core.dom.SimpleName;
import org.exoplatform.ide.java.client.core.dom.rewrite.ASTRewrite;
import org.exoplatform.ide.runtime.CoreException;
import org.exoplatform.ide.text.Document;

public class AddImportCorrectionProposal extends ASTRewriteCorrectionProposal
{

   private final String fTypeName;

   private final String fQualifierName;

   public AddImportCorrectionProposal(String name, int relevance, Document document, Image image, String qualifierName,
      String typeName, SimpleName node)
   {
      super(name, ASTRewrite.create(node.getAST()), relevance, document, image);
      fTypeName = typeName;
      fQualifierName = qualifierName;
   }

   public String getQualifiedTypeName()
   {
      return fQualifierName + '.' + fTypeName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.ui.text.correction.ChangeCorrectionProposal#performChange(org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IDocument)
    */
   @Override
   protected void performChange(Document document) throws CoreException
   {
      super.performChange(document);
      rememberSelection();
   }

   private void rememberSelection()
   {
      QualifiedTypeNameHistory.remember(getQualifiedTypeName());
   }

}
