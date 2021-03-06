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
package com.codenvy.ide.ui.dialogs.input;

import com.codenvy.ide.ui.UILocalizationConstant;
import com.codenvy.ide.ui.dialogs.BaseTest;
import com.google.gwt.user.client.Element;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.codenvy.ide.ui.dialogs.input.InputDialogView.ActionDelegate;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link InputDialogViewImpl} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class InputDialogViewTest extends BaseTest {
    @Mock
    private ActionDelegate         actionDelegate;
    @Mock
    private UILocalizationConstant uiLocalizationConstant;
    @Mock
    private InputDialogFooter      footer;
    private InputDialogViewImpl    view;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        when(footer.getElement()).thenReturn(mock(Element.class));
        view = new InputDialogViewImpl(footer, uiLocalizationConstant);
    }

    @Test
    public void shouldSetDelegateOnFooter() throws Exception {
        view.setDelegate(actionDelegate);

        verify(footer).setDelegate(eq(actionDelegate));
    }
}
