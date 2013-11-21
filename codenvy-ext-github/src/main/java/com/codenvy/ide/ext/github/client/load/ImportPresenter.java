/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.github.client.GitHubClientService;
import com.codenvy.ide.ext.github.client.GitHubLocalizationConstant;
import com.codenvy.ide.ext.github.client.marshaller.AllRepositoriesUnmarshaller;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.security.oauth.OAuthStatus.LOGGED_IN;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Presenter for importing user's GitHub project to IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 */
@Singleton
public class ImportPresenter implements ImportView.ActionDelegate, OAuthCallback {
    private ImportView                                 view;
    private GitHubClientService                        service;
    private GitClientService                           gitService;
    private EventBus                                   eventBus;
    private JsonStringMap<JsonArray<GitHubRepository>> repositories;
    private ProjectData                                selectedRepository;
    private String                                     restContext;
    private GitHubLocalizationConstant                    constant;
    private GitLocalizationConstant                    gitConstant;
    private ResourceProvider                           resourceProvider;
    private ConsolePart                                console;
    private NotificationManager                        notificationManager;
    private Notification                               notification;
    private DtoFactory                                 dtoFactory;


    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param restContext
     * @param constant
     * @param resourceProvider
     * @param console
     * @param gitService
     * @param notificationManager
     */
    @Inject
    public ImportPresenter(ImportView view, GitHubClientService service,GitClientService gitService, EventBus eventBus, @Named("restContext") String restContext,
                           GitHubLocalizationConstant constant, GitLocalizationConstant gitConstant, ResourceProvider resourceProvider, ConsolePart console,
                           NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.gitService = gitService;
        this.eventBus = eventBus;
        this.restContext = restContext;
        this.constant = constant;
        this.gitConstant = gitConstant;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog(User user) {
        getToken(user.getUserId());
    }

    /** Get the list of all authorized user's repositories. */
    private void getUserRepos() {
        try {
            service.getAllRepositories(new AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>(new AllRepositoriesUnmarshaller(dtoFactory)) {
                @Override
                protected void onSuccess(JsonStringMap<JsonArray<GitHubRepository>> result) {
                    onListLoaded(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception.getMessage().contains("Bad credentials")) {
                        Window.alert("Looks like a problem with your SSH key.  Delete a GitHub key at Window > Preferences > " +
                                     "SSH Keys, and try importing your GitHub projects again.");
                    } else {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        Notification notification = new Notification(exception.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Perform actions when the list of repositories was loaded.
     *
     * @param repositories
     *         loaded list of repositories
     */
    private void onListLoaded(@NotNull JsonStringMap<JsonArray<GitHubRepository>> repositories) {
        this.repositories = repositories;

        view.setAccountNames(repositories.getKeys());
        view.setEnableFinishButton(false);

        refreshProjectList();

        view.showDialog();
    }

    /** Refresh project list on view. */
    private void refreshProjectList() {
        JsonArray<ProjectData> projectsData = JsonCollections.createArray();

        String accountName = view.getAccountName();
        JsonArray<GitHubRepository> repo = repositories.get(accountName);

        for (GitHubRepository repository : repo.asIterable()) {
            ProjectData projectData = new ProjectData(repository.getName(), repository.getDescription(), null, null, repository.getSshUrl(),
                                                      repository.getGitUrl());
            projectsData.add(projectData);
        }

        view.setRepositories(projectsData);
        view.setProjectName("");
        selectedRepository = null;
    }

    /**
     * Return token for user.
     *
     * @param user
     *         user which need token
     */
    private void getToken(final String user) {
        try {
            service.getUserToken(user, new AsyncRequestCallback<String>(new com.codenvy.ide.rest.StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    if (result == null || result.isEmpty()) {
                        oAuthLoginStart(user);
                    } else {
                        getUserRepos();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    oAuthLoginStart(user);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Log in  github */
    private void oAuthLoginStart(@NotNull String user) {
        boolean permitToRedirect = Window.confirm(constant.loginOAuthLabel());
        if (permitToRedirect) {
            String authUrl = "rest/ide/oauth/authenticate?oauth_provider=github"
                             + "&scope=user&userId=" + user + "&scope=repo&redirect_after_login=/ide/" + Utils.getWorkspaceName();
            JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
            authWindow.loginWithOAuth();
        }
    }

    /** Generate github key. */
    public void generateGitHubKey() {
        try {
            AsyncRequestCallback<Void> callback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    getUserRepos();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(ImportPresenter.class, "Can't generate ssh key", exception);
                }
            };

            String url = restContext + '/' + Utils.getWorkspaceName() + "/github/ssh/generate";
            AsyncRequest.build(POST, url).loader(new EmptyLoader()).send(callback);
        } catch (RequestException e) {
            Window.alert("Upload key to github failed.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishClicked() {
        final String projectName = view.getProjectName();
        boolean hasProjectNameIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName) || projectName.isEmpty();
        if (selectedRepository != null) {
            if (hasProjectNameIncorrectSymbol) {
                Window.alert(gitConstant.noIncorrectProjectNameMessage());
            } else {
                String remoteUri = selectedRepository.getRepositoryUrl();
                if (!remoteUri.endsWith(".git")) {
                    remoteUri += ".git";
                }

                notification = new Notification(gitConstant.cloneStarted(projectName, remoteUri), PROGRESS);
                notificationManager.showNotification(notification);

                final String finalRemoteUri = remoteUri;
                resourceProvider.createProject(projectName, JsonCollections.<Property>createArray(), new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        cloneRepository(finalRemoteUri, projectName, result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        String errorMessage =
                                (caught.getMessage() != null && caught.getMessage().length() > 0) ? caught.getMessage()
                                                                                                  : gitConstant.cloneFailed(finalRemoteUri);
                        notification.setStatus(FINISHED);
                        notification.setType(ERROR);
                        notification.setMessage(errorMessage);
                    }
                });
            }
        }
    }

    /**
     * Get the necessary parameters values and clone repository (over WebSocket or HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param project
     *         folder (root of GIT repository)
     */
    private void cloneRepository(@NotNull final String remoteUri, @NotNull String remoteName, @NotNull final Project project) {
      try {
            gitService.cloneRepositoryWS(resourceProvider.getVfsId(), project, remoteUri, remoteName,
                                         new RequestCallback<String>(new com.codenvy.ide.ext.git.client.marshaller.StringUnmarshaller()) {
                                             @Override
                                             protected void onSuccess(String result) {
                                                 RepoInfo repoInfo = dtoFactory.createDtoFromJson(result, RepoInfo.class);
                                                 onCloneSuccess(repoInfo, project);
                                             }

                                             @Override
                                             protected void onFailure(Throwable exception) {
                                                 deleteFolder(project);
                                                 handleError(exception, remoteUri);
                                             }
                                         });
            view.close();
        } catch (WebSocketException e) {
            cloneRepositoryREST(remoteUri, remoteName, project);
        }
    }

    /**
     * Get the necessary parameters values and call the clone repository method (over HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param project
     *         folder (root of GIT repository)
     */
    private void cloneRepositoryREST(@NotNull final String remoteUri, @NotNull String remoteName, @NotNull final Project project) {
       try {
            gitService.cloneRepository(resourceProvider.getVfsId(), project, remoteUri, remoteName,
                                       new AsyncRequestCallback<String>(new com.codenvy.ide.rest.StringUnmarshaller()) {
                                           @Override
                                           protected void onSuccess(String result) {
                                               RepoInfo repoInfo = dtoFactory.createDtoFromJson(result, RepoInfo.class);
                                               onCloneSuccess(repoInfo, project);
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               deleteFolder(project);
                                               handleError(exception, remoteUri);
                                           }
                                       });
        } catch (RequestException e) {
            deleteFolder(project);
            handleError(e, remoteUri);
        }
        view.close();
    }

    /**
     * Perform actions when repository was successfully cloned.
     *
     * @param project
     *         {@link Project} to clone
     */
   private void onCloneSuccess(@NotNull final RepoInfo gitRepositoryInfo, @NotNull final Project project) {
        resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                notification.setStatus(FINISHED);
                notification.setMessage(gitConstant.cloneSuccess(gitRepositoryInfo.getRemoteUri()));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ImportPresenter.class, "Can not get project " + project.getName());
            }
        });
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     * @param remoteUri
     *         rempote uri
     */
    private void handleError(@NotNull Throwable e, @NotNull String remoteUri) {
      String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : gitConstant.cloneFailed(remoteUri);
        notification.setStatus(FINISHED);
        notification.setType(ERROR);
        notification.setMessage(errorMessage);
    }

    /**
     * Delete project.
     *
     * @param path
     *         the path where project exist
     */
    private void deleteFolder(@NotNull Project path) {
        resourceProvider.delete(path, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionThrownEvent(caught, "Exception during project removing"));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRepositorySelected(@NotNull ProjectData repository) {
        selectedRepository = repository;
        view.setProjectName(selectedRepository.getName());
        view.setEnableFinishButton(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onAccountChanged() {
        refreshProjectList();
    }

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateGitHubKey();
        }
    }
}