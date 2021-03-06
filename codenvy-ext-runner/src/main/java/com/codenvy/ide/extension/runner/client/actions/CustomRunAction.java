/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.extension.runner.client.run.customrun.CustomRunPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to run project on runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunAction extends ProjectAction {

    private final AppContext           appContext;
    private final AnalyticsEventLogger eventLogger;
    private final RunController        runController;
    private final CustomRunPresenter   customRunPresenter;

    @Inject
    public CustomRunAction(RunController runController,
                           CustomRunPresenter customRunPresenter,
                           RunnerResources resources,
                           RunnerLocalizationConstant localizationConstants,
                           AppContext appContext,
                           AnalyticsEventLogger eventLogger) {
        super(localizationConstants.customRunAppActionText(),
              localizationConstants.customRunAppActionDescription(),
              resources.launchApp());
        this.runController = runController;
        this.customRunPresenter = customRunPresenter;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        customRunPresenter.showDialog();
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            e.getPresentation().setEnabledAndVisible(currentProject.getIsRunningEnabled() && !runController.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
