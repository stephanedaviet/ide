/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.resources.marshal.JSONSerializer.PROPERTY_SERIALIZER;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of {@link TutorialsClientService}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsClientServiceImpl.java Jul 3, 2013 12:50:30 PM azatsarynnyy $
 */
@Singleton
public class TutorialsClientServiceImpl implements TutorialsClientService {
    /** Base url. */
    private static final String BASE_URL                     = '/' + Utils.getWorkspaceName() + "/tutorials";
    /** Create sample project method's path. */
    private static final String CREATE_DTO_TUTORIAL          = "/dto";
    private static final String CREATE_NOTIFICATION_TUTORIAL = "/notification";
    private static final String CREATE_ACTION_TUTORIAL       = "/action";
    private static final String CREATE_WIZARD_TUTORIAL       = "/wizard";
    /** REST-service context. */
    private String           restContext;
    /** Loader to be displayed. */
    private Loader           loader;
    /** Provider of Codenvy IDE resources. */
    private ResourceProvider resourceProvider;

    /**
     * Creates service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     * @param resourceProvider
     *         provider of IDE resources
     */
    @Inject
    protected TutorialsClientServiceImpl(@Named("restContext") String restContext, Loader loader,
                                         ResourceProvider resourceProvider) {
        this.loader = loader;
        this.restContext = restContext;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void createDTOTutorialProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE_DTO_TUTORIAL;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" +
                             resourceProvider.getRootId();
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createNotificationTutorialProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE_NOTIFICATION_TUTORIAL;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" +
                             resourceProvider.getRootId();
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createActionTutorialProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE_ACTION_TUTORIAL;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" +
                             resourceProvider.getRootId();
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createWizardTutorialProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE_WIZARD_TUTORIAL;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" +
                             resourceProvider.getRootId();
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }
}
