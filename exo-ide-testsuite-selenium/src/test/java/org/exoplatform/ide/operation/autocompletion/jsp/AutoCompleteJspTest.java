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
package org.exoplatform.ide.operation.autocompletion.jsp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AutoCompleteJspTest Apr 26, 2011 11:07:34 AM evgen $
 * 
 */
public class AutoCompleteJspTest extends CodeAssistantBaseTest
{

   private static final String FILE_NAME = "JSPtest.jsp";

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         createProject(AutoCompleteJspTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_JSP,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testJsp.jsp");
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }

      openProject();
      openFile(FILE_NAME);
   }

   
   @Test
   public void testAutocompleteJsp() throws Exception
   {
      IDE.GOTOLINE.goToLine(6);
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("background-attachment"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("counter-increment"));
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("!important"));

      IDE.GOTOLINE.goToLine(11);

      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("application:javax.servlet.ServletContext"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("config:javax.servlet.ServletConfig"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("exception:java.lang.Throwable"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("out:javax.servlet.jsp.JspWriter"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("page:java.lang.Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("pageContext:javax.servlet.jsp.PageContext"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("request:javax.servlet.http.HttpServletRequest"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("response:javax.servlet.http.HttpServletResponse"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("session:javax.servlet.http.HttpSession"));

      IDE.CODEASSISTANT.closeForm();
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.EDITOR.typeTextIntoEditor(0, "Collection");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Collection"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Collections"));
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("Collection"));

      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.GOTOLINE.goToLine(18);

      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("a"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Window"));
      IDE.CODEASSISTANT.closeForm();

      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.GOTOLINE.goToLine(24);

      IDE.EDITOR.typeTextIntoEditor(0, "<t");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("table"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("textarea"));
      IDE.CODEASSISTANT.closeForm();

      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.GOTOLINE.goToLine(4);

      IDE.EDITOR.typeTextIntoEditor(0, "<jsp:");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:attribute"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:body"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:element"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:fallback"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:forward"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:getProperty"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:include"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:invoke"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:output"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:plugin"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:text"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:useBean"));

      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.typeTextIntoEditor(0, "<jsp:use");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.typeToInput("\n");
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("<jsp:useBean id=\"\"></jsp:useBean>"));
   }
}