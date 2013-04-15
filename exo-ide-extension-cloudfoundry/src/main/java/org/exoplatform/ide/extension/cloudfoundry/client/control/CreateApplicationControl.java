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
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationEvent;

/**
 * Control for creating application on CloudFoundry.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateApplicationControl.java Jul 7, 2011 5:32:27 PM vereshchaka $
 */
public class CreateApplicationControl extends AbstractCloudFoundryControl implements ProjectOpenedHandler,
                                                                                     ProjectClosedHandler, ActiveProjectChangedHandler {

    private static final String ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlId();

    private static final String TITLE = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlTitle();

    private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlPrompt();

    public CreateApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudFoundryClientBundle.INSTANCE.createApp(), CloudFoundryClientBundle.INSTANCE.createAppDisabled());
        setEvent(new CreateApplicationEvent());
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.control.AbstractCloudFoundryControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(event.getProject() != null && CloudFoundryExtension.canBeDeployedToCF(event.getProject()));
    }

    @Override
    public void onActiveProjectChanged(ActiveProjectChangedEvent event) {
        setEnabled(event.getProject() != null && CloudFoundryExtension.canBeDeployedToCF(event.getProject()));
    }
}