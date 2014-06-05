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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * View of {@link RunnerConsolePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface RunnerConsoleView extends View<RunnerConsoleView.ActionDelegate> {
    public interface ActionDelegate extends BaseActionDelegate {
    }

    /** @return toolbar panel */
    AcceptsOneWidget getToolbarPanel();

    /**
     * Print text in console area.
     *
     * @param text
     *         text that need to be shown
     */
    void print(String text);

    /**
     * Set title of console part.
     *
     * @param title
     *         title that need to be set
     */
    void setTitle(String title);

    /** Clear console. Remove all messages. */
    void clear();

    /** Scroll to bottom of the view. */
    void scrollBottom();
}