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
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerExtension;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for customizing running the project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunPresenter implements CustomRunView.ActionDelegate {
    private RunnerController           runnerController;
    private RunnerServiceClient        runnerServiceClient;
    private CustomRunView              view;
    private DtoFactory                 dtoFactory;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private NotificationManager        notificationManager;
    private AppContext                 appContext;
    private RunnerLocalizationConstant constant;

    /** Create presenter. */
    @Inject
    protected CustomRunPresenter(RunnerController runnerController,
                                 RunnerServiceClient runnerServiceClient,
                                 CustomRunView view,
                                 DtoFactory dtoFactory,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 NotificationManager notificationManager,
                                 AppContext appContext,
                                 RunnerLocalizationConstant constant) {
        this.runnerController = runnerController;
        this.runnerServiceClient = runnerServiceClient;
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.appContext = appContext;
        this.constant = constant;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<Array<RunnerDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<RunnerDescriptor> result) {
                        CurrentProject activeProject = appContext.getCurrentProject();
                        view.setEnvironments(getEnvironmentsForProject(activeProject, result));
                        setMemoryFields();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(constant.gettingEnvironmentsFailed(), ERROR));
                    }
                }
                                      );
    }

    private void setMemoryFields() {
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                int runnerMemory = 0;
                Map<String, String> preferences = appContext.getProfile().getPreferences();
                if (preferences != null && preferences.containsKey(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT)) {
                    try {
                        runnerMemory = Integer.parseInt(preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }
                int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());
                runnerMemory = ((runnerMemory > 0) && (runnerMemory % 128 == 0)) ? runnerMemory : 256;

                view.setRunnerMemorySize(runnerMemory);
                view.setTotalMemorySize(totalMemory);
                view.setAvailableMemorySize(totalMemory - usedMemory);
                view.showDialog();
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showNotification(new Notification(constant.getResourcesFailed(), ERROR));
            }
        });
    }

    private Array<RunnerEnvironment> getEnvironmentsForProject(CurrentProject project, Array<RunnerDescriptor> runners) {
        Array<RunnerEnvironment> environments = Collections.createArray();
        final String runnerName = project.getRunner();
        for (RunnerDescriptor runnerDescriptor : runners.asIterable()) {
            if (runnerName.equals(runnerDescriptor.getName())) {
                for (RunnerEnvironment environment : runnerDescriptor.getEnvironments().values()) {
                    environments.add(environment);
                }
                break;
            }
        }
        return environments;
    }

    @Override
    public void onRunClicked() {
        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        runOptions.setMemorySize(view.getRunnerMemorySize());
        runOptions.setSkipBuild(view.isSkipBuildSelected());

        if (view.getSelectedEnvironment() != null) {
            runOptions.setEnvironmentId(view.getSelectedEnvironment().getId());
        }
        view.close();
        runnerController.runActiveProject(runOptions, true);
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

}
