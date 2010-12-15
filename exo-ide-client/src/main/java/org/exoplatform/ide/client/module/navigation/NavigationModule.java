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
package org.exoplatform.ide.client.module.navigation;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.editor.custom.OpenFileWithForm;
import org.exoplatform.ide.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.navigation.action.CreateFolderForm;
import org.exoplatform.ide.client.module.navigation.action.DeleteItemForm;
import org.exoplatform.ide.client.module.navigation.action.GetItemURLForm;
import org.exoplatform.ide.client.module.navigation.action.RenameItemForm;
import org.exoplatform.ide.client.module.navigation.control.CopyItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.CutItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.DeleteItemCommand;
import org.exoplatform.ide.client.module.navigation.control.GetFileURLControl;
import org.exoplatform.ide.client.module.navigation.control.GoToFolderControl;
import org.exoplatform.ide.client.module.navigation.control.OpenFileWithCommand;
import org.exoplatform.ide.client.module.navigation.control.PasteItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.module.navigation.control.RenameItemCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveAllFilesCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileAsCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileAsTemplateCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileCommand;
import org.exoplatform.ide.client.module.navigation.control.SearchFilesCommand;
import org.exoplatform.ide.client.module.navigation.control.ViewItemPropertiesCommand;
import org.exoplatform.ide.client.module.navigation.control.download.DownloadFileCommand;
import org.exoplatform.ide.client.module.navigation.control.download.DownloadZippedFolderCommand;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateProjectFromTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateProjectTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFileCommandMenuGroup;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.module.navigation.control.upload.OpenFileByPathCommand;
import org.exoplatform.ide.client.module.navigation.control.upload.OpenLocalFileCommand;
import org.exoplatform.ide.client.module.navigation.control.upload.UploadFileCommand;
import org.exoplatform.ide.client.module.navigation.control.upload.UploadFolderControl;
import org.exoplatform.ide.client.module.navigation.control.versioning.RestoreToVersionControl;
import org.exoplatform.ide.client.module.navigation.control.versioning.ViewNextVersionControl;
import org.exoplatform.ide.client.module.navigation.control.versioning.ViewPreviousVersionControl;
import org.exoplatform.ide.client.module.navigation.control.versioning.ViewVersionHistoryControl;
import org.exoplatform.ide.client.module.navigation.control.versioning.ViewVersionListControl;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemEvent;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemHandler;
import org.exoplatform.ide.client.module.navigation.event.GetFileURLEvent;
import org.exoplatform.ide.client.module.navigation.event.GetFileURLHandler;
import org.exoplatform.ide.client.module.navigation.event.OpenFileWithEvent;
import org.exoplatform.ide.client.module.navigation.event.OpenFileWithHandler;
import org.exoplatform.ide.client.module.navigation.event.RenameItemEvent;
import org.exoplatform.ide.client.module.navigation.event.RenameItemHander;
import org.exoplatform.ide.client.module.navigation.event.SaveFileAsTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.SaveFileAsTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.SearchFilesEvent;
import org.exoplatform.ide.client.module.navigation.event.SearchFilesHandler;
import org.exoplatform.ide.client.module.navigation.event.edit.CopyItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CopyItemsHandler;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFolderEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFolderHandler;
import org.exoplatform.ide.client.module.navigation.event.upload.OpenFileByPathEvent;
import org.exoplatform.ide.client.module.navigation.event.upload.OpenFileByPathHandler;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileEvent;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileHandler;
import org.exoplatform.ide.client.module.navigation.handler.CreateFileCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.CreateFileFromTemplateCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.CreateProjectFromTemplateCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.CreateProjectTemplateCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.module.navigation.handler.GoToFolderCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.OpenFileCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.PasteItemsCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.RestoreToVersionCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.SaveAllFilesCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.SaveFileAsCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.SaveFileCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.ShowVersionListCommandHandler;
import org.exoplatform.ide.client.module.navigation.handler.VersionHistoryCommandHandler;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.client.permissions.ShowPermissionsCommandHandler;
import org.exoplatform.ide.client.permissions.control.ShowPermissionsControl;
import org.exoplatform.ide.client.search.file.SearchForm;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.client.template.SaveAsTemplateForm;
import org.exoplatform.ide.client.upload.OpenFileByPathForm;
import org.exoplatform.ide.client.upload.OpenLocalFileForm;
import org.exoplatform.ide.client.upload.UploadFileForm;
import org.exoplatform.ide.client.upload.UploadForm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class NavigationModule implements IDEModule, OpenFileWithHandler, UploadFileHandler, SaveFileAsTemplateHandler,
   CreateFolderHandler, CopyItemsHandler, CutItemsHandler, RenameItemHander, DeleteItemHandler, SearchFilesHandler,
   GetFileURLHandler, ApplicationSettingsReceivedHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler, EntryPointChangedHandler, ConfigurationReceivedSuccessfullyHandler,
   EditorActiveFileChangedHandler, InitializeServicesHandler, OpenFileByPathHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   protected Handlers handlers;

   private ApplicationSettings applicationSettings;

   private IDEConfiguration applicationConfiguration;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private String entryPoint;

   private File activeFile;

   private Map<String, String> lockTokens;

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();

      eventBus.fireEvent(new RegisterControlEvent(newFilePopupMenuControl, true));
      eventBus.fireEvent(new RegisterControlEvent(new NewFileCommandMenuGroup()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFileFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New XML File", "XML File",
         "Create New XML File", Images.FileTypes.XML, MimeType.TEXT_XML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New HTML File", "HTML File",
         "Create New HTML File", Images.FileTypes.HTML, MimeType.TEXT_HTML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New TEXT File", "Text File",
         "Create New Text File", Images.FileTypes.TXT, MimeType.TEXT_PLAIN)));
      eventBus.fireEvent(new RegisterControlEvent(
         new NewItemControl("File/New/New Java Script File", "JavaScript File", "Create New Java Script File",
            Images.FileTypes.JAVASCRIPT, MimeType.APPLICATION_JAVASCRIPT)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New CSS File", "CSS File",
         "Create New CSS File", Images.FileTypes.CSS, MimeType.TEXT_CSS)));
      eventBus.fireEvent(new RegisterControlEvent(new OpenFileWithCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new ViewItemPropertiesCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionHistoryControl(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionListControl(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewPreviousVersionControl(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewNextVersionControl(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new RestoreToVersionControl(), true, true));

      eventBus.fireEvent(new RegisterControlEvent(new UploadFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new UploadFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new OpenLocalFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new OpenFileByPathCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadZippedFolderCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new SaveAllFilesCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsTemplateCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new CutItemsCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new CopyItemsCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new PasteItemsCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new RenameItemCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteItemCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new SearchFilesCommand(), true));
      eventBus.fireEvent(new RegisterControlEvent(new RefreshBrowserControl(), true));
      eventBus.fireEvent(new RegisterControlEvent(new GoToFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new GetFileURLControl()));
      eventBus.fireEvent(new RegisterControlEvent(new NavigatorStatusControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new ShowPermissionsControl()));

      handlers.addHandler(InitializeServicesEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      handlers.addHandler(OpenFileWithEvent.TYPE, this);
      handlers.addHandler(OpenFileByPathEvent.TYPE, this);
      handlers.addHandler(UploadFileEvent.TYPE, this);
      handlers.addHandler(SaveFileAsTemplateEvent.TYPE, this);
      handlers.addHandler(CreateFolderEvent.TYPE, this);
      handlers.addHandler(CopyItemsEvent.TYPE, this);
      handlers.addHandler(CutItemsEvent.TYPE, this);

      handlers.addHandler(DeleteItemEvent.TYPE, this);
      handlers.addHandler(RenameItemEvent.TYPE, this);
      handlers.addHandler(SearchFilesEvent.TYPE, this);
      handlers.addHandler(GetFileURLEvent.TYPE, this);

      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(ItemsSelectedEvent.TYPE, this);

      //      handlers.addHandler(ItemLockedEvent.TYPE, this);
      //      handlers.addHandler(ItemUnlockedEvent.TYPE, this);

      new CreateFileCommandHandler(eventBus);
      new CreateFileFromTemplateCommandHandler(eventBus);
      new CreateProjectFromTemplateCommandHandler(eventBus);
      new OpenFileCommandHandler(eventBus);
      new SaveFileCommandHandler(eventBus);
      new SaveFileAsCommandHandler(eventBus);
      new SaveAllFilesCommandHandler(eventBus);
      new GoToFolderCommandHandler(eventBus);
      new PasteItemsCommandHandler(eventBus, context);
      new FileClosedHandler(eventBus);
      new ShowVersionListCommandHandler(eventBus);
      new VersionHistoryCommandHandler(eventBus);
      new RestoreToVersionCommandHandler(eventBus);
      new CreateProjectTemplateCommandHandler(eventBus);
      new ShowPermissionsCommandHandler(eventBus);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new WebDavVirtualFileSystem(eventBus, event.getLoader(), ImageUtil.getIcons(), event
         .getApplicationConfiguration().getContext());
   }

   public void onOpenFileWith(OpenFileWithEvent event)
   {
      new OpenFileWithForm(eventBus, (File)selectedItems.get(0), openedFiles, applicationSettings);
   }

   public void onUploadFile(UploadFileEvent event)
   {
      String path = "";

      if (selectedItems == null || selectedItems.size() == 0)
      {
         if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType())
                  || UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
         {
            Dialogs.getInstance().showInfo(
               "Please, select target folder in the Workspace Panel before calling this command !");
            return;
         }
      }
      else
      {
         Item item = selectedItems.get(0);

         path = item.getHref();
         if (item instanceof File)
         {
            path = path.substring(path.lastIndexOf("/"));
         }
      }
      eventBus.fireEvent(new ClearFocusEvent());
      if (UploadFileEvent.UploadType.OPEN_FILE.equals(event.getUploadType()))
      {
         new OpenLocalFileForm(eventBus, selectedItems, path, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType()))
      {
         new UploadFileForm(eventBus, selectedItems, path, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
      {
         new UploadForm(eventBus, selectedItems, path, applicationConfiguration);
      }
      
   }

   public void onSaveFileAsTemplate(SaveFileAsTemplateEvent event)
   {
      new SaveAsTemplateForm(eventBus, activeFile);
   }

   public void onCreateFolder(CreateFolderEvent event)
   {
      Item item = selectedItems.get(0);

      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      new CreateFolderForm(eventBus, selectedItems.get(0), href);
   }

   public void onCopyItems(CopyItemsEvent event)
   {
      context.getItemsToCopy().clear();
      context.getItemsToCut().clear();
      context.getItemsToCopy().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onCutItems(CutItemsEvent event)
   {
      context.getItemsToCut().clear();
      context.getItemsToCopy().clear();

      context.getItemsToCut().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onRenameItem(RenameItemEvent event)
   {
      new RenameItemForm(eventBus, selectedItems, openedFiles, lockTokens);
   }

   public void onDeleteItem(DeleteItemEvent event)
   {
      new DeleteItemForm(eventBus, selectedItems, openedFiles, lockTokens);
   }

   public void onSearchFiles(SearchFilesEvent event)
   {
      new SearchForm(eventBus, selectedItems, entryPoint);
   }

   public void onGetFileURL(GetFileURLEvent event)
   {
      String url = selectedItems.get(0).getHref();
      new GetItemURLForm(eventBus, url);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   public void onOpenFileByPath(OpenFileByPathEvent event)
   {
      new OpenFileByPathForm(eventBus);
   }

}
