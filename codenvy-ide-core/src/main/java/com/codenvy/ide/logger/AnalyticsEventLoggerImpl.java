/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.logger;

import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.ide.api.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.dto.EventParameters;
import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * API to track ide usage.
 *
 * @author Anatoliy Bazko
 */
@Singleton
public class AnalyticsEventLoggerImpl implements AnalyticsEventLogger {
    private static final String EVENT_NAME         = "ide-usage";
    private static final String API_ANALYTICS_PATH = "/api/analytics/log/" + EVENT_NAME;

    private static final String WS_PARAM           = "WS";
    private static final String USER_PARAM         = "USER";
    private static final String SOURCE_PARAM       = "SOURCE";
    private static final String ACTION_PARAM       = "ACTION";
    private static final String PROJECT_NAME_PARAM = "PROJECT_NAME";
    private static final String PROJECT_TYPE_PARAM = "PROJECT_TYPE";

    private static final String EMPTY_PARAM_VALUE = "";

    private static final int MAX_PARAMS_NUMBER      = 3;
    private static final int MAX_PARAM_NAME_LENGTH  = 10;
    private static final int MAX_PARAM_VALUE_LENGTH = 20;
    private static final int MAX_ACTION_NAME_LENGTH = 50;

    private final DtoFactory               dtoFactory;
    private final UserProfileServiceClient userProfile;
    private final ResourceProvider         resourceProvider;
    private final MessageBus               messageBus;
    private final ExtensionRegistry        extensionRegistry;

    @Inject
    public AnalyticsEventLoggerImpl(DtoFactory dtoFactory,
                                    ExtensionRegistry extensionRegistry,
                                    UserProfileServiceClient userProfile,
                                    ResourceProvider resourceProvider,
                                    MessageBus messageBus) {
        this.dtoFactory = dtoFactory;
        this.userProfile = userProfile;
        this.resourceProvider = resourceProvider;
        this.messageBus = messageBus;
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    public void log(String action, Class<?> extensionClass, Map<String, String> additionalParams) {
        doLog(action, getSource(extensionClass), additionalParams);
    }

    @Override
    public void log(String action) {
        doLog(action, EMPTY_PARAM_VALUE, Collections.<String, String>emptyMap());
    }

    private void doLog(String action, String source, Map<String, String> additionalParams) {
        validate(action, additionalParams);

        additionalParams = new HashMap<>(additionalParams);
        putReservedParameters(action, source, additionalParams);

        send(additionalParams);
    }

    private void putReservedParameters(String action, String source, Map<String, String> additionalParams) {
        addProjectParams(additionalParams);
        additionalParams.put(WS_PARAM, Utils.getWorkspaceName());
        additionalParams.put(ACTION_PARAM, action);
        additionalParams.put(SOURCE_PARAM, source);
    }

    private void validate(String action, Map<String, String> additionalParams) throws IllegalArgumentException {
        if (additionalParams.size() > MAX_PARAMS_NUMBER) {
            throw new IllegalArgumentException("The number of parameters exceeded the limit in " + MAX_PARAMS_NUMBER);
        }

        for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
            String param = entry.getKey();
            String value = entry.getValue();

            if (param.length() > MAX_PARAM_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        "The length of parameter name " + param + " exceeded the length in "
                        + MAX_PARAM_NAME_LENGTH + " characters");

            } else if (value.length() > MAX_PARAM_VALUE_LENGTH) {
                throw new IllegalArgumentException(
                        "The length of parameter value " + value + " exceeded the length in "
                        + MAX_PARAM_VALUE_LENGTH + " characters");
            }
        }

        if (action.length() > MAX_ACTION_NAME_LENGTH) {
            throw new IllegalArgumentException("The length of action name exceeded the length in "
                                               + MAX_ACTION_NAME_LENGTH + " characters");

        }
    }

    private String getSource(Class<?> extensionClass) {
        ExtensionDescription description = extensionRegistry.getExtensionDescriptions().get(extensionClass.getName());
        return description != null ? description.getTitle() : EMPTY_PARAM_VALUE;
    }

    private void addProjectParams(final Map<String, String> additionalParams) {
        Project project = resourceProvider.getActiveProject();
        if (project != null) {
            additionalParams.put(PROJECT_NAME_PARAM, project.getName());
            additionalParams.put(PROJECT_TYPE_PARAM, project.getDescription().getProjectTypeId());
        } else {
            additionalParams.put(PROJECT_NAME_PARAM, EMPTY_PARAM_VALUE);
            additionalParams.put(PROJECT_TYPE_PARAM, EMPTY_PARAM_VALUE);
        }
    }

    private void send(final Map<String, String> additionalParams) {
        userProfile.getCurrentProfile(null, new AsyncRequestCallback<Profile>() {
            @Override
            protected void onSuccess(Profile result) {
                if (result != null) {
                    additionalParams.put(USER_PARAM, result.getUserId());
                } else {
                    additionalParams.put(USER_PARAM, EMPTY_PARAM_VALUE);
                }

                try {
                    doSend(additionalParams);
                } catch (WebSocketException e) {
                    Log.error(getClass(), e);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                additionalParams.put(USER_PARAM, EMPTY_PARAM_VALUE);

                try {
                    doSend(additionalParams);
                } catch (WebSocketException e) {
                    Log.error(getClass(), e);
                }
            }

            private void doSend(Map<String, String> parameters) throws WebSocketException {
                EventParameters additionalParams = dtoFactory.createDto(EventParameters.class).withParams(parameters);

                MessageBuilder builder = new MessageBuilder(POST, API_ANALYTICS_PATH);
                builder.data(dtoFactory.toJson(additionalParams));
                builder.header(CONTENTTYPE, APPLICATION_JSON);
                Message message = builder.build();

                messageBus.send(message, new RequestCallback() {
                    @Override
                    protected void onSuccess(Object result) {
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(getClass(), exception);
                    }
                });
            }
        });
    }
}