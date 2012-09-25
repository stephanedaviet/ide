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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for {@link EC2ManagerView}. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2Manager.java Sep 21, 2012 10:13:36 AM azatsarynnyy $
 *
 */
public class EC2Manager implements ViewClosedHandler, ShowEC2ManagerHandler
{
   interface Display extends IsView
   {
      void setInstances(List<InstanceInfo> instanceList);

      HasClickHandlers getTerminateInstanceButton();

      HasClickHandlers getRebootInstanceButton();

      HasClickHandlers getStopInstanceButton();

      HasClickHandlers getStartInstanceButton();
   }

   private Display display;

   private ProjectModel openedProject;

   public EC2Manager()
   {
      IDE.getInstance().addControl(new EC2ManagerControl());

      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowEC2ManagerEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
//      display.getCloseButton().addClickHandler(new ClickHandler()
//      {
//         @Override
//         public void onClick(ClickEvent event)
//         {
//            IDE.getInstance().closeView(display.asView().getId());
//         }
//      });
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
    * @see org.exoplatform.ide.extension.aws.client.ec2.ShowEC2ManagerHandler#onShowEC2Manager(org.exoplatform.ide.extension.aws.client.ec2.ShowEC2ManagerEvent)
    */
   @Override
   public void onShowEC2Manager(ShowEC2ManagerEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }

      try
      {
         List<InstanceInfo> instanceList = new ArrayList<InstanceInfo>();
         EC2ClientService.getInstance().instances(
            new AsyncRequestCallback<List<InstanceInfo>>(new InstanceListUnmarshaller(instanceList))
            {

               @Override
               protected void onSuccess(List<InstanceInfo> result)
               {
                  display.setInstances(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO Auto-generated method stub
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
