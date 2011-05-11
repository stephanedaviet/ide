/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.browse.locks;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Check, is can open locked file in CK editor.
 * If open if CK editor locked file, you can't save it.
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 15, 2010 $
 *
 */
public class OpenLockedFileInCKEditorTest extends LockFileAbstract
{

   private static final String FOLDER_NAME = OpenLockedFileInCKEditorTest.class.getSimpleName();

   private static final String FILE_NAME = "file-" + OpenLockedFileInCKEditorTest.class.getSimpleName();

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FOLDER_NAME + "/" + FILE_NAME);
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

   @AfterClass
   public static void tierDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
   public void testOpenLockedFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      //----- 1 ----------
      //open file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //----- 2 ----------
      //lock file
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNLOCK_FILE, true);
      checkFileLocking(FILE_NAME, false);

      //----- 3 ----------
      //delete lock tokens from cookies and refresh
      deleteLockTokensCookies();
      refresh();

      //----- 4 ----------
      //check is file locked
      waitForRootElement();
      Thread.sleep(1000);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      //Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.NAVIGATION.selectItem(URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(URL + FOLDER_NAME + "/" + FILE_NAME);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LUCK_UNLOCK_FILE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.LOCK_FILE, false);

      checkFileLocking(URL + FOLDER_NAME + "/" + FILE_NAME, true);

      //----- 5 ----------
      //close file
      IDE.EDITOR.closeTab(0);

      //----- 6 ----------
      //open file in CK editor and check is file locked
      openFileFromNavigationTreeWithCkEditor(URL + FOLDER_NAME + "/" + FILE_NAME, "HTML", false);
      checkFileLocking(URL + FOLDER_NAME + "/" + FILE_NAME, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.LOCK_FILE, false);

      IDE.EDITOR.typeTextIntoEditor(0, "Test editor");

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);

      IDE.EDITOR.closeTab(0);
      assertFalse(selenium.isElementPresent("exoAskDialog"));
   }

}
