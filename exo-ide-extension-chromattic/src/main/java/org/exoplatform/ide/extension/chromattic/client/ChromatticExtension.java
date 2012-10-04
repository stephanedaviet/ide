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
package org.exoplatform.ide.extension.chromattic.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.chromattic.client.controls.DeployNodeTypeControl;
import org.exoplatform.ide.extension.chromattic.client.controls.GenerateNodeTypeControl;
import org.exoplatform.ide.extension.chromattic.client.model.service.ChrommaticServiceImpl;
import org.exoplatform.ide.extension.chromattic.client.ui.DeployNodeTypePresenter;
import org.exoplatform.ide.extension.chromattic.client.ui.GenerateNodeTypePresenter;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 28, 2010 $
 * 
 */
public class ChromatticExtension extends Extension implements InitializeServicesHandler
{

   public static final ChromatticLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(ChromatticLocalizationConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize()
   {
      IDE.getInstance().addControl(new GenerateNodeTypeControl(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new DeployNodeTypeControl(), Docking.TOOLBAR_RIGHT);
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      new GenerateNodeTypePresenter();
      new DeployNodeTypePresenter();
   }

   /**
    * Initialisation of services
    * 
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new ChrommaticServiceImpl(event.getApplicationConfiguration().getContext(), event.getLoader());
   }

}