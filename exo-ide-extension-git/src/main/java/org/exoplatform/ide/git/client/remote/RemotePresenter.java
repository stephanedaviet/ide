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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.shared.Remote;

import java.util.List;

/**
 * Presenter for view to work with remote repository list (view, add and delete).
 * View must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 18, 2011 11:13:30 AM anya $
 *
 */
public class RemotePresenter extends GitPresenter implements ShowRemotesHandler, ViewClosedHandler
{
   public interface Display extends IsView
   {
      /**
       * Get add button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getAddButton();

      /**
       * Get delete button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getDeleteButton();

      /**
       * Get close button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCloseButton();

      /**
       * Get list grid with remote repositories.
       * 
       * @return {@link ListGridItem}
       */
      ListGridItem<Remote> getRemoteGrid();

      /**
       * Get the selected remote repository in list grid.
       * 
       * @return {@link Remote} selected remote repository
       */
      Remote getSelectedRemote();

      /**
       * Change the enabled state of the delete button.
       * 
       * @param enable enabled state
       */
      void enableDeleteButton(boolean enable);

   }

   /**
    * Presenter's display.
    */
   private Display display;

   public RemotePresenter(HandlerManager eventBus)
   {
      super(eventBus);

      eventBus.addHandler(ShowRemotesEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getAddButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doAdd();
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            askToDelete();
         }
      });

      display.getRemoteGrid().addSelectionHandler(new SelectionHandler<Remote>()
      {

         @Override
         public void onSelection(SelectionEvent<Remote> event)
         {
            boolean selected = event.getSelectedItem() != null;
            display.enableDeleteButton(selected);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.ShowRemotesHandler#onShowRemotes(org.exoplatform.ide.git.client.remote.ShowRemotesEvent)
    */
   @Override
   public void onShowRemotes(ShowRemotesEvent event)
   {
      getWorkDir();
   }

   /**
    * Get the list of remote repositories for local one.
    * If remote repositories are found, then get the list of 
    * branches (remote and local).
    * 
    * @param workDir
    */
   public void getRemotes(final String workDir)
   {
      GitClientService.getInstance().remoteList(workDir, null, true, new AsyncRequestCallback<List<Remote>>()
      {
         @Override
         protected void onSuccess(List<Remote> result)
         {
            if (display == null)
            {
               Display d = GWT.create(Display.class);
               IDE.getInstance().openView(d.asView());
               bindDisplay(d);
            }

            display.getRemoteGrid().setValue(result);
            display.enableDeleteButton(false);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.remoteListFailed();
            Dialogs.getInstance().showError(errorMessage);
         }
      });
   }

   /**
    * Show UI for adding remote repository.
    */
   private void doAdd()
   {
      new AddRemoteRepositoryPresenter(null, "Add remote repository")
      {

         @Override
         public void onSubmit()
         {
            String name = getDisplay().getName().getValue();
            String url = getDisplay().getUrl().getValue();
            addRemoteRepository(name, url);
         }
      };
   }

   /**
    * Add new remote repository to the list of remote repositories.
    * 
    * @param name name
    * @param url url
    */
   private void addRemoteRepository(String name, String url)
   {
      if (workDir == null)
         return;
      GitClientService.getInstance().remoteAdd(workDir, name, url, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            getRemotes(workDir);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.remoteAddFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Ask the user to confirm the deletion of the remote repository.
    */
   private void askToDelete()
   {
      if (workDir == null)
         return;
      final Remote selectedRemote = display.getSelectedRemote();
      if (selectedRemote == null)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectRemoteRepositoryFail());
         return;
      }

      Dialogs.getInstance().ask(GitExtension.MESSAGES.deleteRemoteRepositoryTitle(),
         GitExtension.MESSAGES.deleteRemoteRepositoryQuestion(selectedRemote.getName()),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
                  doDelete(selectedRemote.getName());
            }
         });
   }

   /**
    * Delete remote repository from the list of the remote repositories.
    * 
    * @param name name of the remote repository to delete
    */
   private void doDelete(String name)
   {
      GitClientService.getInstance().remoteDelete(workDir, name, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            getRemotes(workDir);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.remoteDeleteFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getRemotes(workDir);
   }
}
