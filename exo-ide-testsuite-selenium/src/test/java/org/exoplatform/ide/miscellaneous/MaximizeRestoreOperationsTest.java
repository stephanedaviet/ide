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
package org.exoplatform.ide.miscellaneous;

import static org.exoplatform.ide.CloseFileUtils.closeUnsavedFileAndDoNotSave;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.exoplatform.ide.Locators.*;

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

import java.io.IOException;

/**
 * IDE-97:One-click maximize/restore for editor and actions view.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class MaximizeRestoreOperationsTest extends BaseTest
{
   private final static String FILE_NAME =MaximizeRestoreOperationsTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" 
            + WS_NAME + "/" + FILE_NAME;
   
   private Number operationPanelNormalWidth;
   private Number operationPanelNormalHeight;
   
   private Number contentPanelNormalWidth;
   private Number contentPanelNormalHeight;
   
   private Number navigationPanelNormalWidth;
   private Number navigationPanelNormalHeight;
   
   private Number codeHelperPanelNormalWidth;
   private Number codeHelperPanelNormalHeight;
   
   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/miscellaneous/SampleHtmlFile.html";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL);
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
   
   //IDE-97:One-click maximize/restore for editor and actions view
   @Test
   public void maximizeRestoreForEditorAndActionsView() throws Exception
   {
      //prepare file
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);     
           
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 1 -----------------
      //Create, save and open new file in the content panel. 
      //Click on "Show Properties" button to open "Properties" Tab.
      runToolbarButton(ToolbarCommands.View.SHOW_PROPERTIES);
      Thread.sleep(TestConstants.SLEEP);
      
      //store width and height of elements in default perspective
      operationPanelNormalWidth = selenium.getElementWidth(OPERATION_PANEL_LOCATOR);
      operationPanelNormalHeight = selenium.getElementHeight(OPERATION_PANEL_LOCATOR);
      
      contentPanelNormalWidth = selenium.getElementWidth(CONTENT_PANEL_LOCATOR);
      contentPanelNormalHeight =  selenium.getElementHeight(CONTENT_PANEL_LOCATOR);
      
      navigationPanelNormalWidth = selenium.getElementWidth(NAVIGATION_PANEL_LOCATOR);
      navigationPanelNormalHeight = selenium.getElementHeight(NAVIGATION_PANEL_LOCATOR);
      
      //there is new file opened in the file tab of Content Panel. 
      //There is Properties Tab opened in the bottom part of Content Panel. 
      checkCodeEditorOpened(0);
      //check, properties tab appeared
      assertTrue(selenium.isElementPresent("//div[@class='tabBar']//td[@class='tabTitleSelected']/span[contains(text(), 'Properties')]"));
      
      //---- 2 -----------------
      //Click on "Maximize" button at the header of Properties Tab.
      clickMaximizeInOperationsPanel();
      //there is Properties Tab opened on the all gadget window under the top menu and toolbar. 
      //There are no vertical and horizontal delimeters at the left and bottom side of gadget window.
      checkOperationsPanelMaximized();
      checkContentPanelVisibility(false);
      checkNavigationPanelVisibility(false);
      
      //---- 3 -----------------
      //Click on "Restore" button at the header of Properties Tab.
      clickMinimizeInOperationsPanel();
      //restored initial view the same as after the step 1.
      checkOperationsPanelRestored();
      checkContentPanelRestored();
      checkContentPanelVisibility(true);
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      
      //---- 4 -----------------
      //Click on "Maximize" button at the header of Content Panel.
      clickMaximizeInContentPanel();
      //there is File Tab opened on the all gadget window under the top menu and toolbar. 
      //There are no vertical and horizontal delimeters at the left and bottom side of gadget window.
      checkContentPanelMaximized();
      checkNavigationPanelVisibility(false);
      checkOperationsPanelVisibility(false);
      
      //TODO: no delimeters at the left and bottom side of gadget window.
      
      //---- 5 -----------------
      //Click on "Restore" button at the header of Content Panel.
      clickMinimizeInContentPanel();
      //restored initial view the same as after the step 1.
      checkContentPanelRestored();
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      checkOperationsPanelRestored();
      checkOperationsPanelVisibility(true);
      
      //---- 6 -----------------
      //Click on "Maximize" button at the header of Content Panel again.
      clickMaximizeInContentPanel();
      //there is File Tab opened on the all gadget window under the top menu and toolbar. 
      //There are no vertical and horizontal delimeters at the left and bottom side of gadget window.
      checkContentPanelMaximized();
      checkNavigationPanelVisibility(false);
      checkOperationsPanelVisibility(false);
      //TODO: no delimeters at the left and bottom side of gadget window.
      
      //---- 7 -----------------
      //Click on "Show Properties" button.
      runToolbarButton(ToolbarCommands.View.SHOW_PROPERTIES);
      Thread.sleep(TestConstants.SLEEP);
      
      //there is Properties Tab under the file tab and with horizontal delimeter between them, 
      //and "Workspace" panel at the left side.
      checkContentPanelRestored();
      checkOperationsPanelRestored();
      checkOperationsPanelVisibility(true);
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      
      //---- 8 -----------------
      //Click on "Maximize" button at the header of Properties Tab.
      clickMaximizeInOperationsPanel();
      
      //---- 9 -----------------
      //Click on "File->Search" topmenu item and then click on 
      //"Search" button within the "Search" dialog window.
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SEARCH);
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//IButton[ID=\"ideSearchFormSearchButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      
      //there is Properties Tab under the file tab and gadget should display 
      //Search Tab at the left side with search results.
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      checkContentPanelRestored();
      checkContentPanelVisibility(true);
      checkOperationsPanelRestored();
      checkOperationsPanelVisibility(true);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideNavigationTabSet\"]/tab[ID=SearchResultPanel]"));
      checkSearchTabSelected();
      
      //---- 10 -----------------
      //Click on Show Outline Panel
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      //check code helper panel visible
      assertTrue(selenium.isElementPresent(CODE_HELPER_PANEL_LOCATOR));
      
      operationPanelNormalWidth = selenium.getElementWidth(OPERATION_PANEL_LOCATOR);
      contentPanelNormalWidth = selenium.getElementWidth(CONTENT_PANEL_LOCATOR);
      codeHelperPanelNormalWidth = selenium.getElementWidth(CODE_HELPER_PANEL_LOCATOR);
      codeHelperPanelNormalHeight = selenium.getElementHeight(CODE_HELPER_PANEL_LOCATOR);
      
//      checkCodeHelperPanelVisibility(true);
      
      //---- 11 -----------------
      //click maximize in CodeHelpder Panel (near Outline tab)
      clickMaximizeInCodeHelperPanel();
      
      //check outline tab is maximized
      checkCodeHelperPanelMaximized();
      checkNavigationPanelVisibility(false);
      //check vertical layout (contains content panel and operations panel) is hidden
      assertTrue(selenium.isElementPresent(VERTICAL_SPLIT_LAYOUT_LOCATOR + "[contains(@style, 'visibility: hidden')]"));
//      checkContentPanelVisibility(false);
//      checkOperationsPanelVisibility(false);
      
      //---- 12 -----------------
      //click minimize near outline tab
      clickMinimizeInCodeHelperPanel();
      //check restored perspective
      checkCodeHelperPanelRestored();
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      checkContentPanelRestored();
      checkContentPanelVisibility(true);
      checkOperationsPanelRestored();
      checkOperationsPanelVisibility(true);

      //---- 13 -----------------
      //select Workspace tab
      selectWorkspaceTab();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //---- 14 -----------------
      //click maximize in content panel
      clickMaximizeInContentPanel();
      //check content panel maximized
      checkContentPanelMaximized();
      checkNavigationPanelVisibility(false);
      checkOperationsPanelVisibility(false);
      checkCodeHelperPanelVisibility(false);
      
      //---- 15 -----------------
      //click Show Outline button
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      
      //outline panel is visible, content panel is visible.
      //Other panels are hidden
      checkContentPanelVisibility(true);
      checkCodeHelperPanelVisibility(true);
      checkNavigationPanelVisibility(false);
      checkOperationsPanelVisibility(false);
      
      //---- 16 -----------------
      //open new xml file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //check content panel is visible, outline is visible,
      //other panels are hidden
      checkContentPanelVisibility(true);
      checkCodeHelperPanelVisibility(true);
      checkNavigationPanelVisibility(false);
      checkOperationsPanelVisibility(false);
      
      //---- 15 -----------------
      //click restore near content panel
      clickMinimizeInContentPanel();
      
      //check, that perspective restored
      checkNavigationPanelRestored();
      checkNavigationPanelVisibility(true);
      checkContentPanelRestored();
      checkContentPanelVisibility(true);
      checkOperationsPanelRestored();
      checkOperationsPanelVisibility(true);
      checkCodeHelperPanelRestored();
      checkCodeHelperPanelVisibility(true);
      
      
      //---- 16 -----------------
      //Close and remove created file.
      closeUnsavedFileAndDoNotSave(1);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      deleteSelectedItems();
      
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void checkSearchTabSelected()
   {
      assertTrue(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR
         + "//td[@class='tabTitleSelected']/span[contains(text(), 'Search')]"));
      assertTrue(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR
         + "//td[@class='tabTitle']/span[contains(text(), 'Workspace')]"));
   }
   
   private void checkContentPanelMaximized()
   {
      //TODO
      assertEquals(selenium.getElementWidth(MAIN_FORM_LOCATOR).intValue(),
         selenium.getElementWidth(CONTENT_PANEL_LOCATOR).intValue() + 5);
      //1 pixel - it is height of delimeter
      assertEquals(selenium.getElementHeight(MAIN_FORM_LOCATOR).intValue(),
         selenium.getElementHeight(CONTENT_PANEL_LOCATOR).intValue() + 1);

   }
   
   private void checkContentPanelRestored()
   {
      assertEquals(contentPanelNormalWidth, selenium.getElementWidth(CONTENT_PANEL_LOCATOR));
      assertEquals(contentPanelNormalHeight, selenium.getElementHeight(CONTENT_PANEL_LOCATOR));
   }
   
   private void checkOperationsPanelMaximized()
   {
      final Number operationWidth = selenium.getElementWidth(OPERATION_PANEL_LOCATOR);
      final Number operationHeight = selenium.getElementHeight(OPERATION_PANEL_LOCATOR);
      //TODO
      assertEquals(selenium.getElementWidth(MAIN_FORM_LOCATOR).intValue(), operationWidth.intValue() + 5);
      assertEquals(selenium.getElementHeight(MAIN_FORM_LOCATOR), operationHeight);
   }
   
   private void checkOperationsPanelRestored()
   {
      assertEquals(operationPanelNormalWidth, selenium.getElementWidth(OPERATION_PANEL_LOCATOR));
      assertEquals(operationPanelNormalHeight, selenium.getElementHeight(OPERATION_PANEL_LOCATOR));
   }
   
   private void checkOperationsPanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium.isElementPresent(OPERATION_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit')]"));
         assertFalse(selenium.isElementPresent(OPERATION_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(OPERATION_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden')]"));
         assertFalse(selenium.isElementPresent(OPERATION_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit')]"));
      }
   }
   
   private void checkNavigationPanelRestored()
   {
      assertEquals(navigationPanelNormalWidth, selenium.getElementWidth(NAVIGATION_PANEL_LOCATOR));
      assertEquals(navigationPanelNormalHeight, selenium.getElementHeight(NAVIGATION_PANEL_LOCATOR));
   }
   
   private void checkNavigationPanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit;')]"));
         assertFalse(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden;')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden;')]"));
         assertFalse(selenium.isElementPresent(NAVIGATION_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit;')]"));
      }
   }
   
   private void checkContentPanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium.isElementPresent(CONTENT_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit;')]"));
         assertFalse(selenium.isElementPresent(CONTENT_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden;')]"));
      }
      else
      {
         
         assertTrue(selenium.isElementPresent(CONTENT_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden;')]"));
         assertFalse(selenium.isElementPresent(CONTENT_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit;')]"));
      }
   }
   
   private void checkCodeHelperPanelMaximized()
   {
      //TODO
      assertEquals(selenium.getElementWidth(MAIN_FORM_LOCATOR).intValue(), 
         selenium.getElementWidth(CODE_HELPER_PANEL_LOCATOR).intValue() + 5);
      assertEquals(selenium.getElementHeight(MAIN_FORM_LOCATOR), selenium.getElementHeight(CODE_HELPER_PANEL_LOCATOR));
   }
   
   private void checkCodeHelperPanelRestored()
   {
      assertEquals(codeHelperPanelNormalWidth, selenium.getElementWidth(CODE_HELPER_PANEL_LOCATOR));
      assertEquals(codeHelperPanelNormalHeight, selenium.getElementHeight(CODE_HELPER_PANEL_LOCATOR));
   }
   
   private void checkCodeHelperPanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium.isElementPresent(CODE_HELPER_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit')]"));
         assertFalse(selenium.isElementPresent(CODE_HELPER_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(CODE_HELPER_PANEL_LOCATOR + "[contains(@style, 'visibility: hidden')]"));
         assertFalse(selenium.isElementPresent(CODE_HELPER_PANEL_LOCATOR + "[contains(@style, 'visibility: inherit')]"));
      }
   }
   
   private void clickMaximizeInOperationsPanel() throws Exception
   {
      selenium.mouseDownAt(OPERATION_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      selenium.mouseUpAt(OPERATION_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void clickMinimizeInOperationsPanel() throws Exception
   {
      selenium.mouseDownAt(OPERATION_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      selenium.mouseUpAt(OPERATION_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void clickMaximizeInContentPanel() throws Exception
   {
      selenium.mouseDownAt(CONTENT_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      selenium.mouseUpAt(CONTENT_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void clickMinimizeInContentPanel() throws Exception
   {
      selenium.mouseDownAt(CONTENT_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      selenium.mouseUpAt(CONTENT_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void clickMaximizeInCodeHelperPanel() throws Exception
   {
      selenium.mouseDownAt(CODE_HELPER_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      selenium.mouseUpAt(CODE_HELPER_PANEL_LOCATOR + "//img[contains(@src, 'maximize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void clickMinimizeInCodeHelperPanel() throws Exception
   {
      selenium.mouseDownAt(CODE_HELPER_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      selenium.mouseUpAt(CODE_HELPER_PANEL_LOCATOR + "//img[contains(@src, 'minimize')]", "2,2");
      Thread.sleep(TestConstants.SLEEP);
   }
   
}
