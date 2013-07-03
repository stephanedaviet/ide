/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.client.beanstalk.wizard;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSExtension;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AWSResource;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.SolutionStackListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.SolutionStack;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class BeanstalkPagePresenter extends AbstractWizardPagePresenter implements BeanstalkPageView.ActionDelegate, ProjectBuiltHandler {
    private BeanstalkPageView       view;
    private EventBus                eventBus;
    private String                  environmentName;
    private Project                 project;
    private ResourceProvider        resourceProvider;
    private ConsolePart             console;
    private AWSLocalizationConstant constant;
    private HandlerRegistration     projectBuildHandler;
    private LoginPresenter          loginPresenter;
    private BeanstalkClientService  service;
    private TemplateAgent           templateAgent;
    private CreateProjectProvider   createProjectProvider;
    private String                  warUrl;
    private String                  projectName;
    private Loader                  loader;
    private boolean                 isLogined;

    @Inject
    public BeanstalkPagePresenter(BeanstalkPageView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                  AWSLocalizationConstant constant, LoginPresenter loginPresenter,
                                  BeanstalkClientService service, TemplateAgent templateAgent,
                                  CreateProjectProvider createProjectProvider, AWSResource resource, Loader loader) {
        super("Deploy project to Elastic Beanstalk", resource.elasticBeanstalk48());

        this.view = view;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.templateAgent = templateAgent;
        this.createProjectProvider = createProjectProvider;
        this.loader = loader;

        this.view.setDelegate(this);
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication();
        }
    }

    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    @Override
    public boolean canFinish() {
        return validate();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return validate();
    }

    @Override
    public String getNotice() {
        if (!isLogined) {
            return "This project will be created without deploy on Elastic Beanstalk.";
        } else if (view.getApplicationName().isEmpty()) {
            return "Please, enter a application's name.";
        } else if (view.getEnvironmentName().isEmpty()) {
            return "Please, enter a environment's name";
        } else if (view.getSolutionStack().isEmpty()) {
            return "Please, select solution stack technology.";
        }

        return null;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        createProjectProvider = templateAgent.getSelectedTemplate().getCreateProjectProvider();
        projectName = createProjectProvider.getProjectName();

        view.setApplicationName(projectName);
        view.setEnvironmentName("");

        getSolutionStack();

        isLogined = true;

        container.setWidget(view);
    }

    public boolean validate() {
        return !isLogined ||
               view.getApplicationName() != null && !view.getApplicationName().isEmpty() && view.getEnvironmentName() != null &&
               !view.getEnvironmentName().isEmpty() && view.getSolutionStack() != null && !view.getSolutionStack().isEmpty();
    }

    private void createApplication() {
        loader.setMessage(constant.creatingProject());
        loader.show();

        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };

        DtoClientImpls.CreateApplicationRequestImpl createApplicationRequest = DtoClientImpls.CreateApplicationRequestImpl.make();
        createApplicationRequest.setApplicationName(projectName);
        createApplicationRequest.setDescription("");
        createApplicationRequest.setS3Bucket("");
        createApplicationRequest.setS3Key("");
        createApplicationRequest.setWar(warUrl);

        DtoClientImpls.ApplicationInfoImpl dtoApplicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(dtoApplicationInfo);

        try {
            service.createApplication(resourceProvider.getVfsId(), project.getId(), createApplicationRequest,
                                      new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              String message = constant.createApplicationFailed(projectName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              console.print(constant.createApplicationSuccess(result.getName()));
                                              createEnvironment(result.getName());
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    private void createEnvironment(final String appName) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createEnvironment(appName);
            }
        };

        loader.setMessage("Creating environment...");
        loader.show();

        DtoClientImpls.CreateEnvironmentRequestImpl createEnvironmentRequest = DtoClientImpls.CreateEnvironmentRequestImpl.make();
        createEnvironmentRequest.setApplicationName(appName);
        createEnvironmentRequest.setDescription("");
        createEnvironmentRequest.setEnvironmentName(environmentName);
        createEnvironmentRequest.setVersionLabel(AWSExtension.INIT_VER_LABEL);
        createEnvironmentRequest.setSolutionStackName(view.getSolutionStack());

        DtoClientImpls.EnvironmentInfoImpl environmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(environmentInfo);

        try {
            service.createEnvironment(resourceProvider.getVfsId(), project.getId(), createEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              loader.hide();
                                              String message = constant.launchEnvironmentFailed(environmentName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              loader.hide();
                                              console.print(constant.launchEnvironmentLaunching(environmentName));

                                              RequestStatusHandler environmentStatusHandler =
                                                      new EnvironmentRequestStatusHandler(
                                                              constant.launchEnvironmentLaunching(result.getName()),
                                                              constant.launchEnvironmentSuccess(result.getName()), eventBus);
                                              new EnvironmentStatusChecker(resourceProvider, project, result, true,
                                                                           environmentStatusHandler, eventBus, console, service,
                                                                           loginPresenter, constant).startChecking();
                                          }
                                      });
        } catch (RequestException e) {
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionThrownEvent(caught));
            }

            @Override
            public void onSuccess(Project result) {
                if (isLogined) {
                    deploy(result);
                }
            }
        });
    }

    public void deploy(Project project) {
        this.project = project;

        buildApplication();
    }

    private void buildApplication() {
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    @Override
    public void onApplicationNameChange() {
        projectName = view.getApplicationName();
    }

    @Override
    public void onEnvironmentNameChange() {
        environmentName = view.getEnvironmentName();
    }

    private void getSolutionStack() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                isLogined = true;
                getSolutionStack();
            }
        };

        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                isLogined = false;
                delegate.updateControls();
            }
        };

        JsonArray<SolutionStack> solutionStack = JsonCollections.createArray();
        SolutionStackListUnmarshaller unmarshaller = new SolutionStackListUnmarshaller(solutionStack);

        try {
            service.getAvailableSolutionStacks(
                    new AwsAsyncRequestCallback<JsonArray<SolutionStack>>(unmarshaller, loggedInHandler, loginCanceledHandler,
                                                                          loginPresenter) {
                        @Override
                        protected void processFail(Throwable exception) {
                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                            console.print(exception.getMessage());
                        }

                        @Override
                        protected void onSuccess(JsonArray<SolutionStack> result) {
                            JsonArray<String> stackList = JsonCollections.createArray();
                            for (int i = 0; i < result.size(); i++) {
                                SolutionStack solution = result.get(i);
                                if (solution.getPermittedFileTypes().contains("war")) {
                                    stackList.add(solution.getName());
                                }
                            }

                            view.setSolutionStack(stackList);
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}