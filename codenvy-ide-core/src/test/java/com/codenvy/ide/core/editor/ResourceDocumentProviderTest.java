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
package com.codenvy.ide.core.editor;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Evgen Vidolob */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDocumentProviderTest {
    @Mock
    private DocumentCallback     callback;
    @Mock
    private EditorInput          input;
    @Mock
    private FileNode             file;
    @Mock
    private EventBus             eventBus;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @Before
    public void setUp() {
        when(input.getFile()).thenReturn(file);
    }

    @Test
    public void shouldCallFileGetContent() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus, projectServiceClient);
        provider.getDocument(input, callback);
        verify(file).getContent(Mockito.<AsyncCallback<String>>any());
    }

    @Test
    public void shouldCallCallback() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus, projectServiceClient);
        doAnswer(createServerResponse()).when(file).getContent((AsyncCallback<String>)any());
        provider.getDocument(input, callback);
        verify(callback).onDocument((Document)any());
    }

    @SuppressWarnings("unchecked")
    private Answer<?> createServerResponse() {
        Answer<?> responseEmulator = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallback<String> callback = (AsyncCallback<String>)invocation.getArguments()[0];
                callback.onSuccess("content");
                return null;
            }
        };
        return responseEmulator;
    }
}
