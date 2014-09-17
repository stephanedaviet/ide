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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.project.shared.Constants;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.extension.runner.client.actions.CustomRunAction;
import com.codenvy.ide.extension.runner.client.actions.EditImagesAction;
import com.codenvy.ide.extension.runner.client.actions.GetLogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.StopAction;
import com.codenvy.ide.extension.runner.client.actions.ViewRecipeAction;
import com.codenvy.ide.extension.runner.client.console.ClearConsoleAction;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.indicators.ApplicationURLIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerFinishedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerStartedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTimeoutThresholdIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTotalTimeIndicator;
import com.codenvy.ide.extension.runner.client.wizard.SelectRunnerPagePresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Runner extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Runner", version = "3.0.0")
public class RunnerExtension {
    public static final String GROUP_RUNNER_CONSOLE_TOOLBAR  = "RunnerConsoleToolbar";
    public static final String GROUP_CUSTOM_IMAGES           = "CustomImagesGroup";
    /** Key for user preference which contains default RAM size. */
    public final static String PREFS_RUNNER_RAM_SIZE_DEFAULT = "runner.ram-size.default";

    @Inject
    public RunnerExtension(RunnerResources runnerResources,
                           ProjectTypeWizardRegistry wizardRegistry,
                           Provider<SelectRunnerPagePresenter> runnerPagePresenter) {
        runnerResources.runner().ensureInjected();

        // TODO: temporary solution to add runner page for Blank project
        ProjectWizard wizard = wizardRegistry.getWizard(Constants.BLANK_ID);
        wizard.addPage(runnerPagePresenter);
        wizardRegistry.addWizard(Constants.BLANK_ID, wizard);
    }

    @Inject
    private void prepareActions(RunnerLocalizationConstant localizationConstants,
                                RunnerResources resources,
                                ActionManager actionManager,
                                RunAction runAction,
                                CustomRunAction customRunAction,
                                EditImagesAction editImagesAction,
                                GetLogsAction getLogsAction,
                                StopAction stopAction,
                                ClearConsoleAction clearConsoleAction,
                                ViewRecipeAction viewRecipeAction) {
        // register actions
        actionManager.registerAction(localizationConstants.runAppActionId(), runAction);
        actionManager.registerAction(localizationConstants.customRunAppActionId(), customRunAction);
        actionManager.registerAction(localizationConstants.editImagesActionId(), editImagesAction);
        actionManager.registerAction(localizationConstants.getAppLogsActionId(), getLogsAction);
        actionManager.registerAction(localizationConstants.stopAppActionId(), stopAction);
        actionManager.registerAction(localizationConstants.viewRecipeActionId(), viewRecipeAction);

        // add actions on main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        runToolbarGroup.add(runAction);
        mainToolbarGroup.add(runToolbarGroup);

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);

        // add 'Custom Images' group
        DefaultActionGroup customImagesGroup = new DefaultActionGroup(localizationConstants.customImagesActionTitle(), true, actionManager);
        customImagesGroup.getTemplatePresentation().setSVGIcon(resources.launchApp());
        actionManager.registerAction(GROUP_CUSTOM_IMAGES, customImagesGroup);
        customImagesGroup.add(editImagesAction);
        customImagesGroup.addSeparator();

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction);
        runMenuActionGroup.add(customRunAction, new Constraints(Anchor.AFTER, localizationConstants.runAppActionId()));
        runMenuActionGroup.add(customImagesGroup, new Constraints(Anchor.AFTER, localizationConstants.customRunAppActionId()));
        runMenuActionGroup.add(getLogsAction);
        runMenuActionGroup.add(stopAction);
        runMenuActionGroup.add(clearConsoleAction);
        runMenuActionGroup.add(viewRecipeAction);
    }

    @Inject
    private void prepareRunnerConsole(ActionManager actionManager,
                                      StopAction stopAction,
                                      ClearConsoleAction clearConsoleAction,
                                      ViewRecipeAction viewRecipeAction,
                                      ApplicationURLIndicator applicationURLIndicator,
                                      RunnerStartedIndicator runnerStartedIndicator,
                                      RunnerTimeoutThresholdIndicator runnerTimeoutThresholdIndicator,
                                      RunnerFinishedIndicator runnerFinishedIndicator,
                                      RunnerTotalTimeIndicator runnerTotalTimeIndicator,
                                      WorkspaceAgent workspaceAgent,
                                      RunnerConsolePresenter runnerConsolePresenter,
                                      @RunnerConsoleToolbar ToolbarPresenter runnerConsoleToolbar) {
        workspaceAgent.openPart(runnerConsolePresenter, PartStackType.INFORMATION, new Constraints(Anchor.AFTER, "Builder"));

        // add toolbar with actions on Runner console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_RUNNER_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(stopAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(viewRecipeAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(applicationURLIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerStartedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerFinishedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTimeoutThresholdIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTotalTimeIndicator);
        runnerConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}
