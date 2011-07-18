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
package org.exoplatform.ide.operation.autocompletion.groovy;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 12:40:45 PM evgen $
 *
 */
public class GroovyObjectCompletionTest extends BaseTest
{

   @Test
   public void testGroovyObjectCompletion() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 10; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }

      selenium().keyDown("//body[@class='editbox']", "\\35");
     IDE.EDITOR.typeTextIntoEditor(0, ".");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("con");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.CODEASSISTANT.checkElementPresent("concat(String):String");
      IDE.CODEASSISTANT.checkElementPresent("contains(CharSequence):boolean");
      IDE.CODEASSISTANT.checkElementPresent("contentEquals(StringBuffer):boolean");
      IDE.CODEASSISTANT.checkElementPresent("contentEquals(CharSequence):boolean");

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains(".contentEquals(StringBuffer)"));
     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

}
