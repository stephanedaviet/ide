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
package org.exoplatform.ide.operation.browse.highlight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.thoughtworks.selenium.Wait;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 *
 */
public class ChangeHighlightTest extends BaseTest
{

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private static String FOLDER_NAME = ChangeHighlightTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
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
   public void testChangeHighlihtTest() throws Exception
   {
      waitForRootElement();
      IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      waitForElementPresent("//div[@panel-id='editor']");
      IDE.PERSPECTIVE.checkViewIsActive("editor-0");
      IDE.PERSPECTIVE.checkViewIsNotActive("ideWorkspaceView");

      IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.PERSPECTIVE.checkViewIsNotPresent("editor-0");
      IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");
      IDE.WORKSPACE.selectItem(URL + FOLDER_NAME + "/");
      IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      saveAsUsingToolbarButton("Gadget");
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      waitForElementNotPresent("//div[@view-id='gadgetpreview']");

      IDE.PREVIEW.checkPreviewGadgetIsOpened(true);
      IDE.PERSPECTIVE.checkViewIsActive("gadgetpreview");
      IDE.WORKSPACE.selectItem(URL + FOLDER_NAME + "/");
      IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");

   }

   @AfterClass
   public static void tierDown()
   {
      deleteCookies();
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

}
