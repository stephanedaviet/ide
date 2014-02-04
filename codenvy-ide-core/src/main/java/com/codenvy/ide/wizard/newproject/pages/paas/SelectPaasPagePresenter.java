/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.newproject.pages.paas;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.TemplateClientService;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.wizard.newproject.PaaSAgentImpl;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;

/** @author Evgen Vidolob */
public class SelectPaasPagePresenter extends AbstractWizardPage implements SelectPaasPageView.ActionDelegate {

    private SelectPaasPageView    view;
    private ResourceProvider      resourceProvider;
    private TemplateClientService templateClientService;
    private PaaSAgentImpl         paasAgent;
    private DtoFactory            dtoFactory;
    private Array<PaaS>           paases;

    @Inject
    public SelectPaasPagePresenter(SelectPaasPageView view, ResourceProvider resourceProvider, TemplateClientService templateClientService,
                                   PaaSAgentImpl paasAgent, DtoFactory dtoFactory) {
        super("Select PaaS", null);
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.templateClientService = templateClientService;
        this.paasAgent = paasAgent;
        this.dtoFactory = dtoFactory;
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return wizardContext.getData(PAAS) != null;
    }

    @Override
    public void focusComponent() {
        this.paases = paasAgent.getPaaSes();
        this.view.setPaases(paases);
        ProjectTypeDescriptor projectType = wizardContext.getData(PROJECT_TYPE);
        boolean isFirst = true;
        for (int i = 0; i < paases.size(); i++) {
            PaaS paas = paases.get(i);
            boolean isAvailable = paas.isAvailable(projectType.getProjectTypeId());
            view.setEnablePaas(i, isAvailable);
            if (isAvailable && isFirst) {
                onPaaSSelected(i);
                isFirst = false;
            }
        }
    }

    @Override
    public void removeOptions() {
        // nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public void onPaaSSelected(int id) {
        PaaS paas = paases.get(id);
        wizardContext.putData(PAAS, paas);

        view.selectPaas(id);

        delegate.updateControls();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void commit(final CommitCallback callback) {
        final String projectName = wizardContext.getData(PROJECT_NAME);
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(TEMPLATE);
        try {
            templateClientService.createProject(projectName, templateDescriptor, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(final String result) {
                    resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project project) {
                            wizardContext.putData(PROJECT, dtoFactory.createDtoFromJson(result, ProjectDescriptor.class));
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }
}
