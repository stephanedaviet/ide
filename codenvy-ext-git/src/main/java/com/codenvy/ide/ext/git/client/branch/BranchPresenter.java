/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_ALL;

/**
 * Presenter for displaying and work with branches.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 8, 2011 12:02:49 PM anya $
 */
@Singleton
public class BranchPresenter implements BranchView.ActionDelegate {
    private BranchView              view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private Branch                  selectedBranch;
    private Project                 project;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param console
     */
    @Inject
    public BranchPresenter(BranchView view, GitClientService service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        view.setEnableCheckoutButton(false);
        view.setEnableDeleteButton(false);
        view.setEnableRenameButton(false);
        getBranches(project.getId());
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameClicked() {
        final String currentBranchName = selectedBranch.getDisplayName();
        String name = Window.prompt(constant.branchTypeNew(), currentBranchName);
        if (!name.isEmpty()) {
            final String projectId = project.getId();
            try {
                service.branchRename(resourceProvider.getVfsId(), projectId, currentBranchName, name, new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        getBranches(projectId);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage =
                                (exception.getMessage() != null) ? exception.getMessage() : constant.branchRenameFailed();
                        console.print(errorMessage);
                    }
                });
            } catch (RequestException e) {
                String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.branchRenameFailed();
                console.print(errorMessage);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        final String name = selectedBranch.getName();
        boolean needToDelete = Window.confirm(constant.branchDeleteAsk(name));
        if (needToDelete) {
            final String projectId = project.getId();
            try {
                service.branchDelete(resourceProvider.getVfsId(), projectId, name, true, new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        getBranches(projectId);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.branchDeleteFailed();
                        console.print(errorMessage);
                    }
                });
            } catch (RequestException e) {
                String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.branchDeleteFailed();
                console.print(errorMessage);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCheckoutClicked() {
        String name = selectedBranch.getDisplayName();
        String startingPoint = null;
        boolean remote = selectedBranch.remote();
        if (remote) {
            startingPoint = selectedBranch.getDisplayName();
        }
        final String projectId = project.getId();
        if (name == null) {
            return;
        }

        try {
            service.branchCheckout(resourceProvider.getVfsId(), projectId, name, startingPoint, remote, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            getBranches(projectId);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(BranchPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.branchCheckoutFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.branchCheckoutFailed();
            console.print(errorMessage);
        }
    }

    /**
     * Get the list of branches.
     *
     * @param projectId
     *         project id
     */
    public void getBranches(@NotNull String projectId) {
        BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller();

        try {
            service.branchList(resourceProvider.getVfsId(), projectId, LIST_ALL, new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<Branch> result) {
                    view.setBranches(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.branchesListFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.branchesListFailed();
            console.print(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        String name = Window.prompt(constant.branchTypeNew(), "");
        if (!name.isEmpty()) {
            final String projectId = project.getId();

            try {
                service.branchCreate(resourceProvider.getVfsId(), projectId, name, null, new AsyncRequestCallback<Branch>() {
                    @Override
                    protected void onSuccess(Branch result) {
                        getBranches(projectId);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.branchCreateFailed();
                        console.print(errorMessage);
                    }
                });
            } catch (RequestException e) {
                String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.branchCreateFailed();
                console.print(errorMessage);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBranchSelected(@NotNull Branch branch) {
        selectedBranch = branch;
        boolean enabled = !selectedBranch.active();
        view.setEnableCheckoutButton(enabled);
        view.setEnableDeleteButton(true);
        view.setEnableRenameButton(true);
    }
}