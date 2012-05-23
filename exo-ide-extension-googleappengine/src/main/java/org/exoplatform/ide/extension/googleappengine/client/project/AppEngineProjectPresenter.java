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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 22, 2012 5:14:40 PM anya $
 * 
 */
public class AppEngineProjectPresenter extends GoogleAppEnginePresenter implements ManageAppEngineProjectHandler
{

   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();

      HasClickHandlers getConfigureBackendButton();

      HasClickHandlers getDeleteBackendButton();

      HasClickHandlers getUpdateBackendButton();

      HasClickHandlers getRollbackBackendButton();

      HasClickHandlers getRollbackAllBackendsButton();

      HasClickHandlers getLogsButton();

      HasClickHandlers getUpdateButton();
      
      HasClickHandlers getRollbackButton();

      HasClickHandlers getUpdateCronButton();

      HasClickHandlers getUpdateDosButton();

      HasClickHandlers getUpdateIndexesButton();

      HasClickHandlers getVacuumIndexesButton();

      HasClickHandlers getUpdatePageSpeedButton();

      HasClickHandlers getUpdateQueuesButton();
   }
   
   private Display display;
   
   public AppEngineProjectPresenter()
   {
      IDE.getInstance().addControl(new AppEngineProjectControl());

      IDE.addHandler(ManageAppEngineProjectEvent.TYPE, this);
   }
   
   public void bindDisplay()
   {
      
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectHandler#onManageAppEngineProject(org.exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectEvent)
    */
   @Override
   public void onManageAppEngineProject(ManageAppEngineProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
   }
}
