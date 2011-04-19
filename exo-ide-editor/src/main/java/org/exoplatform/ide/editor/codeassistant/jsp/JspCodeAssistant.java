/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.codeassistant.jsp;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantFactory;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JSPCodeAssistant Apr 15, 2011 12:34:45 PM evgen $
 *
 */
public class JspCodeAssistant extends JavaCodeAssistant
{

   /**
    * @param service
    * @param factory
    * @param errorHandler
    */
   public JspCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
      JavaCodeAssistantErrorHandler errorHandler)
   {
      super(service, factory, errorHandler);
   }

   /**
    * @see org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      if (MimeType.APPLICATION_JAVA.equals(lineMimeType))
      {
         super.autocompleteCalled(editor, mimeType, cursorOffsetX, cursorOffsetY, lineContent, cursorPositionX,
            cursorPositionY, tokenList, lineMimeType, currentToken);
      }
      else
      {
         CodeAssistantFactory.getCodeAssistant(lineMimeType).autocompleteCalled(editor, mimeType, cursorOffsetX,
            cursorOffsetY, lineContent, cursorPositionX, cursorPositionY, tokenList, lineMimeType, currentToken);
      }
   }
}
