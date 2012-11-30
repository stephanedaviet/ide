/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.prepare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.project.ConvertToProjectHandler;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPreparePresenter implements IDEControl, ConvertToProjectHandler
{
   public interface Display extends IsView
   {

   }

   /**
    * Instance of opened {@link Display}.
    */
   private Display display;

   public ProjectPreparePresenter()
   {
      IDE.addHandler(ConvertToProjectEvent.TYPE, this);
   }

   @Override
   public void onConvertToProject(final ConvertToProjectEvent event)
   {
      Loader loader = new GWTLoader();
      String url =
         Utils.getRestContext() + "/ide/project/prepare?vfsid=" + event.getVfsId() + "&folderid=" + event.getFolderId();

      try
      {
         AsyncRequest.build(RequestBuilder.POST, url, false)
            .loader(loader)
            .send(new AsyncRequestCallback<Void>()
            {
               @Override
               protected void onSuccess(Void result)
               {
                  //Conversion successful, open project
                  IDE.fireEvent(new OutputEvent("Project preparing successful.", OutputMessage.Type.INFO));
                  openPreparedProject(event.getFolderId());
               }

               @Override
               protected void onFailure(Throwable e)
               {
                  if ("autodetection_failed".equals(e.getLocalizedMessage()))
                  {
                     //TODO review this
                     IDE.fireEvent(new ExceptionThrownEvent("Please select project type."));
                  }
                  else
                  {
                     //if some other error appear
                     IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
      }
   }

   private void openPreparedProject(String folderId)
   {
      try
      {
         ProjectModel project = new ProjectModel();
         ItemWrapper item = new ItemWrapper(project);
         ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
         VirtualFileSystem.getInstance().getItemById(folderId, new AsyncRequestCallback<ItemWrapper>(unmarshaller)
         {
            @Override
            protected void onSuccess(ItemWrapper result)
            {
               IDE.fireEvent(new ProjectCreatedEvent((ProjectModel)result.getItem()));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent("Failed to opened prepared project."));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public void initialize()
   {
   }

   /**
    * Creates and binds display.
    */
   private void createAndBindDisplay()
   {
      display = GWT.create(Display.class);
      org.exoplatform.ide.client.framework.module.IDE.getInstance().openView(display.asView());
   }
}
