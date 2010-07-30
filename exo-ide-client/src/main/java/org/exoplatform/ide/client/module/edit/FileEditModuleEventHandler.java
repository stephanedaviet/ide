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
package org.exoplatform.ideall.client.module.edit;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.model.settings.ApplicationSettings;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ideall.client.module.edit.action.GoToLineForm;
import org.exoplatform.ideall.client.module.edit.event.FindTextEvent;
import org.exoplatform.ideall.client.module.edit.event.FindTextHandler;
import org.exoplatform.ideall.client.module.edit.event.GoToLineEvent;
import org.exoplatform.ideall.client.module.edit.event.GoToLineHandler;
import org.exoplatform.ideall.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.module.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.search.text.FindTextForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class FileEditModuleEventHandler implements RegisterEventHandlersHandler, FindTextHandler, GoToLineHandler,
   ShowLineNumbersHandler, ApplicationSettingsReceivedHandler, EditorActiveFileChangedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   public FileEditModuleEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.removeHandler(RegisterEventHandlersEvent.TYPE);

      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(FindTextEvent.TYPE, this);
      handlers.addHandler(GoToLineEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler#onShowLineNumbers(org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent)
    */
   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      applicationSettings.setShowLineNumbers(event.isShowLineNumber());
      CookieManager.setShowLineNumbers(event.isShowLineNumber());
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.FindTextHandler#onFindText(org.exoplatform.ideall.client.event.edit.FindTextEvent)
    */
   public void onFindText(FindTextEvent event)
   {
      new FindTextForm(eventBus, activeFile);
   }

   public void onGoToLine(GoToLineEvent event)
   {
      if (activeFile != null)
      {
         new GoToLineForm(eventBus, activeFile);
      }
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

}
