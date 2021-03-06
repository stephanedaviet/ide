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
package com.codenvy.ide.ui.dialogs.confirm;

import com.codenvy.ide.ui.UILocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ui.dialogs.confirm.ConfirmDialogView.ActionDelegate;
import static com.codenvy.ide.ui.window.Window.Resources;

/**
 * The footer show on confirmation dialogs.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class ConfirmDialogFooter extends Composite {

    private static final Resources                   resources = GWT.create(Resources.class);
    /** The UI binder instance. */
    private static       ConfirmDialogFooterUiBinder uiBinder  = GWT.create(ConfirmDialogFooterUiBinder.class);
    /** The i18n messages. */
    @UiField(provided = true)
    UILocalizationConstant messages;
    @UiField
    Button                 okButton;
    @UiField
    Button                 cancelButton;
    /** The action delegate. */
    private ActionDelegate actionDelegate;

    @Inject
    public ConfirmDialogFooter(final @Nonnull UILocalizationConstant messages) {
        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));

        okButton.addStyleName(resources.centerPanelCss().blueButton());
        okButton.getElement().setId("ask-dialog-ok");
        cancelButton.addStyleName(resources.centerPanelCss().button());
        cancelButton.getElement().setId("ask-dialog-cancel");
    }

    /**
     * Sets the action delegate.
     *
     * @param delegate
     *         the new value
     */
    public void setDelegate(final ActionDelegate delegate) {
        this.actionDelegate = delegate;
    }

    /**
     * Handler set on the OK button.
     *
     * @param event
     *         the event that triggers the handler call
     */
    @UiHandler("okButton")
    public void handleOkClick(final ClickEvent event) {
        this.actionDelegate.accepted();
    }

    /**
     * Handler set on the cancel button.
     *
     * @param event
     *         the event that triggers the handler call
     */
    @UiHandler("cancelButton")
    public void handleCancelClick(final ClickEvent event) {
        this.actionDelegate.cancelled();
    }

    /** The UI binder interface for this component. */
    interface ConfirmDialogFooterUiBinder extends UiBinder<Widget, ConfirmDialogFooter> {
    }
}
