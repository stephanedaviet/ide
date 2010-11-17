/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UploadingGoogleGadgetTest extends BaseTest
{
   
   private static final String FOLDER_NAME = UploadingGoogleGadgetTest.class.getSimpleName();
   
   private static final String FILE_NAME = "gadget.xml";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/gadget.xml";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testUploadGoogleGadget() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      uploadFile(MenuCommands.File.UPLOAD, FILE_PATH, MimeType.GOOGLE_GADGET);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(FILE_NAME);
      String url = getSelectedItemUrl();
      
      assertEquals(URL + "/" + FILE_NAME, url);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      
      assertEquals("exo:googleGadget",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType]/textbox"));
      assertEquals(MimeType.GOOGLE_GADGET,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType]/textbox"));
      
    }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}
