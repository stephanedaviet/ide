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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.internal.corext.util.CodeFormatterUtil;
import org.exoplatform.ide.client.framework.editor.CodeFormatter;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.edits.TextEdit;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 3:18:10 PM Apr 2, 2012 evgen $
 * 
 */
public class JavaCodeFormatter implements CodeFormatter
{

   /**
    * @see org.exoplatform.ide.client.framework.editor.CodeFormatter#format(org.exoplatform.ide.editor.text.IDocument)
    */
   @Override
   public TextEdit format(IDocument document)
   {
      return CodeFormatterUtil.format2(org.eclipse.jdt.client.core.formatter.CodeFormatter.K_COMPILATION_UNIT,
         document.get(), 0, null, JavaCore.getOptions());
   }

}
