/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.privacy;

import com.codenvy.ide.actions.PrivacyAction;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.action.IdeActions;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.constraints.Anchor.AFTER;
import static com.codenvy.ide.api.constraints.Anchor.BEFORE;
import static com.codenvy.ide.api.constraints.Constraints.LAST;

/**
 * This presenter provides the base functionality to add and hide the privacy action with its separators. Currently there is no way to do
 * that in a simple way.
 *
 * @author Kevin Pollet
 */
@Singleton
public class PrivacyPresenter implements ProjectActionHandler {
    private static final String PRIVACY_ACTION_ID = "Privacy";

    private final ActionManager      actionManager;
    private final DefaultActionGroup rightMainMenuGroup;
    private final PrivacyAction      privacyAction;

    @Inject
    public PrivacyPresenter(ActionManager actionManager, PrivacyAction privacyAction, EventBus eventBus) {
        this.actionManager = actionManager;
        this.privacyAction = privacyAction;
        this.rightMainMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RIGHT_MAIN_MENU);

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        actionManager.registerAction(PRIVACY_ACTION_ID, privacyAction);

        rightMainMenuGroup.add(privacyAction, LAST);
        rightMainMenuGroup.add(Separator.getInstance(), new Constraints(BEFORE, PRIVACY_ACTION_ID));
        rightMainMenuGroup.add(Separator.getInstance(), new Constraints(AFTER, PRIVACY_ACTION_ID));
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        int index = 0;
        boolean found = false;

        final Action[] actions = rightMainMenuGroup.getChildActionsOrStubs();
        for (Action oneAction : actions) {
            if (oneAction.equals(privacyAction)) {
                found = true;
                break;
            }
            index++;
        }

        if (found) {
            final Action previousAction = actions[index - 1];
            if (previousAction instanceof Separator) {
                rightMainMenuGroup.remove(previousAction);
            }

            actionManager.unregisterAction(PRIVACY_ACTION_ID);
            rightMainMenuGroup.remove(actions[index]);

            final Action nextAction = actions[index + 1];
            if (previousAction instanceof Separator) {
                rightMainMenuGroup.remove(nextAction);
            }
        }
    }
}
