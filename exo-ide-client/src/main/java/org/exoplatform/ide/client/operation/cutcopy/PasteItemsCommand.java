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
package org.exoplatform.ide.client.operation.cutcopy;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"administrators", "developers"})
public class PasteItemsCommand extends SimpleControl implements IDEControl, ItemsToPasteSelectedHandler,
   PasteItemsCompleteHandler, ItemsSelectedHandler, VfsChangedHandler, ViewActivatedHandler,
   ViewVisibilityChangedHandler, ProjectOpenedHandler, ProjectClosedHandler
{
   public static final String ID = "Edit/Paste Item(s)";

   private final static String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsTitleControl();

   private final static String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsPromptControl();

   private boolean itemsToPasteSelected = false;

   private boolean browserPanelSelected = false;

   private List<Item> selectedItems;

   private VirtualFileSystemInfo vfsInfo;

   private boolean isProjectOpened = false;

   private boolean isBrowserPanelVisible;

   private boolean isProjectExplorerVisible;

   /**
    * 
    */
   public PasteItemsCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.paste(), IDEImageBundle.INSTANCE.pasteDisabled());
      setEvent(new PasteItemsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsToPasteSelectedEvent.TYPE, this);
      IDE.addHandler(PasteItemsCompleteEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedHandler#onItemsToPasteSelected(org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedEvent)
    */
   @Override
   public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event)
   {
      itemsToPasteSelected = true;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.PasteItemsCompleteHandler#onPasteItemsComlete(org.exoplatform.ide.client.navigation.event.PasteItemsCompleteEvent)
    */
   @Override
   public void onPasteItemsComlete(PasteItemsCompleteEvent event)
   {
      itemsToPasteSelected = false;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      updateState();
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      updateState();
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      browserPanelSelected =
         (event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay);
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      View view = event.getView();

      if (view instanceof NavigatorDisplay || view instanceof ProjectExplorerDisplay)
      {
         isBrowserPanelVisible = view.isViewVisible();

         if (view instanceof ProjectExplorerDisplay)
         {
            isProjectExplorerVisible = view.isViewVisible();
         }
      }

      updateState();
   }

   protected void updateState()
   {
      if (vfsInfo == null)
      {
         setVisible(false);
         return;
      }

      if (!isProjectOpened && isProjectExplorerVisible)
      {
         setVisible(false);
         return;
      }

      if (!isBrowserPanelVisible)
      {
         setVisible(false);
         return;
      }

      setVisible(true);

      if (selectedItems == null || selectedItems.size() != 1)
      {
         setEnabled(false);
         return;
      }

      setEnabled(itemsToPasteSelected && browserPanelSelected);
   }

   /**
    * @see org.exoplatform.ide.client.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      isProjectOpened = false;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      isProjectOpened = true;
      updateState();
   }
}
