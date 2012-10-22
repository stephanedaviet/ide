/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.java.client.internal.corext.fix;

import com.google.gwt.core.client.GWT;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface MultiFixMessages extends Messages
{
   MultiFixMessages INSTANCE = GWT.create(MultiFixMessages.class);

   String CodeStyleMultiFix_ConvertSingleStatementInControlBodeyToBlock_description();

   String ControlStatementsCleanUp_RemoveUnnecessaryBlocks_description();

   String ControlStatementsCleanUp_RemoveUnnecessaryBlocksWithReturnOrThrow_description();

   String ExpressionsCleanUp_addParanoiac_description();

   String ExpressionsCleanUp_removeUnnecessary_description();

   String UnimplementedCodeCleanUp_AddUnimplementedMethods_description();

   String UnimplementedCodeCleanUp_MakeAbstract_description();

   String UnusedCodeMultiFix_RemoveUnusedImport_description();

   String UnusedCodeMultiFix_RemoveUnusedMethod_description();

   /**
    * @return
    */
   String UnusedCodeMultiFix_RemoveUnusedConstructor_description();

   /**
    * @return
    */
   String UnusedCodeMultiFix_RemoveUnusedType_description();

   /**
    * @return
    */
   String UnusedCodeMultiFix_RemoveUnusedField_description();

   /**
    * @return
    */
   String UnusedCodeMultiFix_RemoveUnusedVariable_description();

   /**
    * @return
    */
   String CodeStyleMultiFix_AddThisQualifier_description();

   /**
    * @return
    */
   String CodeStyleCleanUp_QualifyNonStaticMethod_description();

   /**
    * @return
    */
   String CodeStyleCleanUp_removeFieldThis_description();

   /**
    * @return
    */
   String CodeStyleCleanUp_removeMethodThis_description();

   /**
    * @return
    */
   String CodeStyleMultiFix_QualifyAccessToStaticField();

   /**
    * @return
    */
   String CodeStyleCleanUp_QualifyStaticMethod_description();

   /**
    * @return
    */
   String CodeStyleMultiFix_ChangeNonStaticAccess_description();

   /**
    * @return
    */
   String CodeStyleMultiFix_ChangeIndirectAccessToStaticToDirect();

   /**
    * @return
    */
   String UnusedCodeCleanUp_RemoveUnusedCasts_description();

}
