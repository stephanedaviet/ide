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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 12, 2010
 *
 */
public class GoToLineTest extends BaseTest
{
   //IDE-152
   //TODO doesn't work on Windows
   @Test
   public void goToLine() throws Exception
   {
      //      Open new Groovy file in editor.
      //TODO***********fix
      Thread.sleep(TestConstants.SLEEP);
      //************************
      runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //     Open new HTML file in editor.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //      Select Groovy file.
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      Thread.sleep(TestConstants.SLEEP);
      selenium.selectFrame("relative=top");
      //      Go to menu and click "View->Go To Line".
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGoToLineForm\"]/headerLabel/"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Enter line number (1..13):"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "1");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Go"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Cancel"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element");
      //     Print "abc" in input field.
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "abc");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals(
         "",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //      Type "100" (above range maximum) and click "Go" button.
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "100");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals(
         "100",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Line number out of range"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("OK"));
      //      Click "Ok".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element");
      //      Type "2" and click "Go" button.
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "2");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertEquals("2 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //      Select HTML file's tab.
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.selectFrame("relative=top");
      //      Go to menu and click "View->Go To Line".
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element");
      //      Enter line number "1" and click "Enter".
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element",
            "1");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP);
      //            Go to status bar - right down corner , where row and column numbers are displayed, hover on them with the mouse and click on it.
      selenium.clickAt("//div[@class='exo-statusBar-panel']//nobr[text()='1 : 1']", "");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isTextPresent("Go"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Cancel"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isTextPresent("Enter line number (1..7):"));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //            Print "2" and click "Go".
      selenium.type("scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[index=2]/element", "2");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/icon");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      Thread.sleep(TestConstants.SLEEP);
   }
}