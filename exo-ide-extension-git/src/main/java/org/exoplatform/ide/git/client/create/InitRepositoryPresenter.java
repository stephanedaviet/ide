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
package org.exoplatform.ide.git.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.create.event.InitRepositoryEvent;
import org.exoplatform.ide.git.client.create.event.InitRepositoryHandler;
import org.exoplatform.ide.git.client.service.GitClientService;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 24, 2011 9:07:58 AM anya $
 *
 */
public class InitRepositoryPresenter implements InitRepositoryHandler, ItemsSelectedHandler
{
   public interface Display extends ViewDisplay
   {
      HasValue<Boolean> getBareValue();

      HasValue<String> getWorkDirValue();

      HasClickHandlers getInitButton();

      HasClickHandlers getCancelButton();
   }

   private Display display;

   private HandlerManager eventBus;

   private List<Item> selectedItems;

   public InitRepositoryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(InitRepositoryEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getInitButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            initRepository();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.getView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.create.event.InitRepositoryHandler#onInitRepository(org.exoplatform.ide.git.client.create.event.InitRepositoryEvent)
    */
   @Override
   public void onInitRepository(InitRepositoryEvent event)
   {
      if (selectedItems == null || selectedItems.size() != 1 || !(selectedItems.get(0) instanceof Folder))
      {
         Dialogs.getInstance().showInfo("Please, select one folder in browser tree.");
         return;
      }

      Display d = GWT.create(Display.class);
      IDE.getInstance().openView((ViewEx)d);
      bindDisplay(d);
      display.getWorkDirValue().setValue(selectedItems.get(0).getHref(), true);
   }

   public void initRepository()
   {
      String workDir = display.getWorkDirValue().getValue();
      boolean bare = display.getBareValue().getValue();
      GitClientService.getInstance().init(workDir, bare, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            Dialogs.getInstance().showInfo("Repository initialization", "Repository was successfully initialized.");
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Dialogs.getInstance().showError("Repository initialization", "Repository was not initialized.");
         }
      });
      IDE.getInstance().closeView(display.getView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }
}
