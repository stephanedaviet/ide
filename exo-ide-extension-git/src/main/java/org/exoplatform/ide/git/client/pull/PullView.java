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
package org.exoplatform.ide.git.client.pull;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

import java.util.LinkedHashMap;

/**
 * View for pulling changes from remote repository to local one. Point view in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 4:23:02 PM anya $
 */
public class PullView extends ViewImpl implements PullPresenter.Display {
    public static final int     HEIGHT                   = 205;

    public static final int     WIDTH                    = 510;

    public static final String  ID                       = "idePullView";

    private static final String PULL_BUTTON_ID           = "idePullViewPullButton";

    private static final String CANCEL_BUTTON_ID         = "idePullViewCancelButton";

    private static final String REMOTE_FIELD_ID          = "idePullViewRemoteField";

    private static final String REMOTE_BRANCHES_FIELD_ID = "idePullViewRemoteBranchesField";

    private static final String LOCAL_BRANCHES_FIELD_ID  = "idePullViewLocalBranchesField";

    /** Pull button. */
    @UiField
    ImageButton                 pullButton;

    /** Cancel button. */
    @UiField
    ImageButton                 cancelButton;

    /** Remote repository field. */
    @UiField
    SelectItem                  remoteField;

    /** Local branches field */
    @UiField
    ComboBoxField               localBranchesField;

    /** Remote branches field. */
    @UiField
    ComboBoxField               remoteBranchesField;

    interface PullViewUiBinder extends UiBinder<Widget, PullView> {
    }

    private static PullViewUiBinder uiBinder = GWT.create(PullViewUiBinder.class);

    public PullView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.pullTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        remoteField.setName(REMOTE_FIELD_ID);
        pullButton.setButtonId(PULL_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        localBranchesField.setName(LOCAL_BRANCHES_FIELD_ID);
        remoteBranchesField.setName(REMOTE_BRANCHES_FIELD_ID);
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getPullButton() */
    @Override
    public HasClickHandlers getPullButton() {
        return pullButton;
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getRemoteName() */
    @Override
    public HasValue<String> getRemoteName() {
        return remoteField;
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getRemoteBranches() */
    @Override
    public HasValue<String> getRemoteBranches() {
        return remoteBranchesField;
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getLocalBranches() */
    @Override
    public HasValue<String> getLocalBranches() {
        return localBranchesField;
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#setRemoteBranches(java.lang.String[]) */
    @Override
    public void setRemoteBranches(String[] values) {
        remoteBranchesField.setValueMap(values);

        if (values != null && values.length > 0) {
            remoteBranchesField.setValue(values[0], true);
        }
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#setLocalBranches(java.lang.String[]) */
    @Override
    public void setLocalBranches(String[] values) {
        localBranchesField.setValueMap(values);

        if (values != null && values.length > 0) {
            localBranchesField.setValue(values[0], true);
        }
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#enablePullButton(boolean) */
    @Override
    public void enablePullButton(boolean enable) {
        pullButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#setRemoteValues(java.util.LinkedHashMap) */
    @Override
    public void setRemoteValues(LinkedHashMap<String, String> values) {
        remoteField.setValueMap(values);
    }

    /** @see org.exoplatform.ide.git.client.pull.PullPresenter.Display#getRemoteDisplayValue() */
    @Override
    public String getRemoteDisplayValue() {
        return remoteField.getDisplayValue();
    }
}
