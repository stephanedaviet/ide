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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Test that file, locked by another user, became unchangable
 * and marked by special icon in navigation tree.
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class LocksByUserTest extends LockFileAbstract
{
   private static String PROJECT = LockFileTest.class.getSimpleName();

   private final static String FOLDER_NAME = LocksByUserTest.class.getSimpleName();

   private final static String FILE_NAME = "file-" + LocksByUserTest.class.getSimpleName();

   @Before
   public void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME);

         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, WS_URL + PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testLocksByUser() throws Exception
   {

      //step 1 open project
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      //step 2 lock file an logout
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      String contentEditor = IDE.EDITOR.getTextFromCodeEditor(0);
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.Editor.LOCK_FILE);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.LOADER.waitClosed();
      checkAllUnlockStateButtons();
      IDE.LOGIN.logout();

      //step 3 login as invite user, open and check lock project 
      IDE.LOGIN.waitTenantLoginPage();
      IDE.LOGIN.loginAsUser();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.EDITOR.waitTabPresent(0);
      IDE.WELCOME_PAGE.close();
      IDE.WELCOME_PAGE.waitClose();

      // open project as invite user
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      //check lock icons
      IDE.LOCK_FILE.isLockIconViewOnFileInProjecrExplorer(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.LOCK_FILE.isLockIconOnTabView(0);
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.Editor.LOCK_FILE);

      //change content and close file
      IDE.GOTOLINE.goToLine(1);
      IDE.EDITOR.deleteFileContent(0);
      IDE.EDITOR.typeTextIntoEditor(0, "Change in locked file");
      assertEquals("Change in locked file", IDE.EDITOR.getTextFromCodeEditor(0));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
      IDE.EDITOR.closeFile(0);
      //ask dialog sholudn't appearance
      assertFalse(IDE.ASK_DIALOG.isOpened());
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
      //reopen file and check. Content should be is not changed 
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      assertEquals(contentEditor, IDE.EDITOR.getTextFromCodeEditor(1));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
   }

   /**
    * check enabled ulock icon and button on toolbar and Edit menu
    * @throws Exception
    */
   private void checkAllUnlockStateButtons() throws Exception
   {
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      assertTrue(IDE.LOCK_FILE.isUnLockCommandActive());
      IDE.MENU.clickOnLockLayer();
      IDE.LOADER.waitClosed();
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.UNLOCK_FILE));
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE));
   }

}