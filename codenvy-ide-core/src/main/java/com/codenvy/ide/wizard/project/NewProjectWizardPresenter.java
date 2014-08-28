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
package com.codenvy.ide.wizard.project;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentConfigurationDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private final ProjectServiceClient      projectService;
    private final DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private final CoreLocalizationConstant  constant;
    private       ProjectTypeWizardRegistry wizardRegistry;
    private       DtoFactory                dtoFactory;
    private       EventBus                  eventBus;
    private       WizardPage                currentPage;
    private       ProjectWizardView         view;
    private       MainPagePresenter         mainPage;
    private Provider<WizardPage> mainPageProvider = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private WizardContext wizardContext;
    private ProjectWizard wizard;

    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view,
                                     MainPagePresenter mainPage,
                                     ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectTypeWizardRegistry wizardRegistry,
                                     CoreLocalizationConstant constant,
                                     DtoFactory dtoFactory,
                                     EventBus eventBus) {
        this.view = view;
        this.mainPage = mainPage;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.constant = constant;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        if (wizard != null) {
            WizardPage wizardPage;
            wizardPage = wizard.flipToNext();
            setPage(wizardPage);
            currentPage.focusComponent();

        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        if (wizard != null) {
            WizardPage wizardPage = wizard.flipToPrevious();
            if (wizardPage == null) {
                wizardPage = mainPage;
            }
            setPage(wizardPage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveClicked() {
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        final WizardPage.CommitCallback callback = new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                view.close();
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                Info info = new Info(exception.getMessage());
                info.show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);

        if (project != null && projectName.equals(project.getName())) {
            updateProject(project, callback);
            return;
        }
        //do check whether there is a project with the same name
        projectService.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                //Project with the same name already exists
                Info info =
                        new Info(constant.createProjectWarningTitle(), constant.createProjectFromTemplateProjectExists(projectName));
                info.show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                //Project with the same name does not exist
                if (wizardContext.getData(ProjectWizard.PROJECT_TYPE) != null && wizardContext.getData(ProjectWizard.PROJECT) == null &&
                    com.codenvy.api.project.shared.Constants.BLANK_ID
                            .equals(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId())) {
                    createBlankProject(callback);
                    return;
                }
                if (templateDescriptor == null && wizard != null) {
                    wizard.onFinish();
                    view.close();
                    return;
                }
                importProject(callback, templateDescriptor, projectName);
            }
        });
    }

    /**
     * This method called during changing project type
     *
     * @param project
     * @param callback
     */
    private void updateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        final boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectDescriptor.setRunner(wizardContext.getData(ProjectWizard.RUNNER_NAME));
        projectDescriptor.setDefaultRunnerEnvironment(wizardContext.getData(ProjectWizard.RUNNER_ENV_ID));
        projectDescriptor.setBuilder(wizardContext.getData(ProjectWizard.BUILDER_NAME));
        view.setLoaderVisibled(true);
        projectService.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                view.setLoaderVisibled(false);
                if (project.getVisibility().equals(visibility)) {
                    getProject(project.getName(), callback);
                } else {
                    switchVisibility(callback, result);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisibled(false);
                callback.onFailure(exception);
            }
        });
    }


    /**
     * This method called after importing new project.
     * In need for changing visibility private/public and setting description from project template
     *
     * @param projectDescriptor
     * @param callback
     */
    private void updateProjectAfterImport(final ProjectDescriptor projectDescriptor, final WizardPage.CommitCallback callback) {
        String description = wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION);
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);

        if (description == null && templateDescriptor != null && templateDescriptor.getDescription() != null) {
            projectDescriptor.setDescription(templateDescriptor.getDescription());
        } else projectDescriptor.setDescription(description);

        view.setLoaderVisibled(true);
        projectService.updateProject(projectDescriptor.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor projectDescriptor) {
                view.setLoaderVisibled(false);
                if (wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY)) {
                    getProject(projectDescriptor.getName(), callback);
                } else {
                    switchVisibility(callback, projectDescriptor);
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                view.setLoaderVisibled(false);
                callback.onFailure(throwable.getCause());
            }
        });
    }

    private void createBlankProject(final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectDescriptor.setRunner(wizardContext.getData(ProjectWizard.RUNNER_NAME));
        projectDescriptor.setDefaultRunnerEnvironment(wizardContext.getData(ProjectWizard.RUNNER_ENV_ID));
        projectDescriptor.setBuilder(wizardContext.getData(ProjectWizard.BUILDER_NAME));
        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        view.setLoaderVisibled(true);
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.createProject(name, projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                view.setLoaderVisibled(false);
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisibled(false);
                callback.onFailure(exception);
            }
        });
    }

    private void importProject(final WizardPage.CommitCallback callback,
                               ProjectTemplateDescriptor templateDescriptor,
                               final String projectName) {
        view.setLoaderVisibled(true);
        projectService.importProject(projectName,
                                     templateDescriptor.getSource(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             view.setLoaderVisibled(false);
                                             updateProjectAfterImport(result, callback);
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             view.setLoaderVisibled(false);
                                             callback.onFailure(exception);
                                         }
                                     }
                                    );
    }

    private void switchVisibility(final WizardPage.CommitCallback callback, final ProjectDescriptor project) {
        String visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY) ? "public" : "private";
        projectService.switchVisibility(project.getPath(), visibility, new AsyncRequestCallback<Void>() {

            @Override
            protected void onSuccess(Void result) {
                getProject(project.getName(), callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void getProject(String name, final WizardPage.CommitCallback callback) {
        eventBus.fireEvent(new OpenProjectEvent(name));
        view.setLoaderVisibled(false);
        callback.onSuccess();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.setLoaderVisibled(false);
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        if (currentPage == mainPage) {
            ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
            if (descriptor != null) {
                wizard = wizardRegistry.getWizard(descriptor.getProjectTypeId());
                if (wizard != null) {
                    wizard.setUpdateDelegate(this);
                    if (!wizard.containsPage(mainPageProvider)) {
                        wizard.addPage(mainPageProvider, 0, false);
                    }
                    wizard.flipToFirst();
                    mainPage.setContext(wizardContext);
                }

            }
        }

        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
        // change state of buttons
        view.setBackButtonEnabled(currentPage != mainPage);
        view.setNextButtonEnabled(wizard != null && wizard.hasNext() && currentPage.isCompleted());
        view.setFinishButtonEnabled((currentPage.isCompleted() && templateDescriptor != null) ||
                                    (templateDescriptor == null && currentPage != mainPage && wizard != null && wizard.canFinish()) ||
                                    (descriptor != null && descriptor.getProjectTypeId().equals(
                                            com.codenvy.api.project.shared.Constants.BLANK_ID) && currentPage.isCompleted()));

        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            view.setRunnerEnvirConfig(null);
            view.setBuilderEnvirConfig(null);
            String requiredMemorySize = null;
            String defaultEnvironment = templateDescriptor.getDefaultRunnerEnvironment();
            Map<String, RunnerEnvironmentConfigurationDescriptor> configurations = templateDescriptor.getRunnerEnvironmentConfigurations();
            if (defaultEnvironment != null && configurations != null) {
                RunnerEnvironmentConfigurationDescriptor configurationDescriptor = configurations.get(defaultEnvironment);
                if (configurationDescriptor != null) {
                    int memorySize = configurationDescriptor.getRecommendedMemorySize();
                    if (memorySize > 0) {
                        if (memorySize < 1000) {
                            requiredMemorySize = memorySize + "Mb";
                        } else {
                            requiredMemorySize = memorySize / 1000 + "Gb";
                        }
                    }
                }
            }
            view.setRAMRequired(requiredMemorySize);
            //set info visible
            view.setInfoVisibled(true);
        } else if (descriptor != null) {

            // TODO: need configuration
            view.setRunnerEnvirConfig(new String[]{"JDK 7.0"});
            view.setBuilderEnvirConfig(new String[]{"JDK 7.0"});

            //leave the default
            view.setRAMRequired(null);
            //set info visible
            view.setInfoVisibled(true);
        } else {
            view.setInfoVisibled(false);
        }

        // TODO: need workspace information
        view.setRAMAvailable(null);

    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        wizardContext.clear();
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, true);
        showFirstPage();
    }

    private void showFirstPage() {
        wizard = null;
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            boolean aPublic = project.getVisibility().equals("public") ? true : false;
            wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_NAME, project.getName());
        }
        setPage(mainPage);
        view.showDialog();
        view.setEnabledAnimation(false);
    }

    public void show(WizardContext context) {
        wizardContext = context;
        showFirstPage();
    }

    /**
     * Change current page and responds other operation which needed for changing page.
     *
     * @param wizardPage
     *         new current page
     */
    private void setPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }
}
