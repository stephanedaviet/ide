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
package com.codenvy.ide.extension.runner.client.manage.ram;

import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/** @author Vitaly Parfonov */
public class RamManagerViewImpl extends Composite implements RamManagerView {
    private RunnerResources            resources;
    private RunnerLocalizationConstant locale;
    private DialogFactory              dialogFactory;

    private ActionDelegate delegate;

    @UiField
    SuggestBox memoryField;

    interface RamManagerViewImplUiBinder extends UiBinder<Widget, RamManagerViewImpl> {
    }

    private static RamManagerViewImplUiBinder ourUiBinder = GWT.create(RamManagerViewImplUiBinder.class);

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     * @param dialogFactory
     */
    @Inject
    protected RamManagerViewImpl(RunnerResources resources, RunnerLocalizationConstant locale, DialogFactory dialogFactory) {
        this.resources = resources;
        this.locale = locale;
        this.dialogFactory = dialogFactory;
        initWidget(ourUiBinder.createAndBindUi(this));
        memoryField.getElement().setAttribute("type", "number");
        memoryField.getElement().setAttribute("step", "128");
        memoryField.getElement().setAttribute("min", "0");
    }

    @Override
    public String getRam() {
        return memoryField.getText();
    }

    @Override
    public void showRam(String ram) {
        memoryField.setText(ram);
    }

    @Override
    public void showWarnMessage(String warning) {
        dialogFactory.createMessageDialog("Warning", warning, null).show();
    }

    @UiHandler("memoryField")
    public void onRamFieldsChanged(ValueChangeEvent<String> valueChangeEvent) {
        delegate.validateRamSize(valueChangeEvent.getValue());
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return this;
    }
}
