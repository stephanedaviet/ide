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
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for application instance.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ApplicationInstanceUnmarshallerWS implements Unmarshallable<ApplicationInstance> {
    private DtoClientImpls.ApplicationInstanceImpl applicationInstance;

    /**
     * Create unmarshaller.
     *
     * @param applicationInstance
     */
    public ApplicationInstanceUnmarshallerWS(@NotNull DtoClientImpls.ApplicationInstanceImpl applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.ApplicationInstanceImpl applicationInstance = DtoClientImpls.ApplicationInstanceImpl.deserialize(text);

        this.applicationInstance.setName(applicationInstance.getName());
        this.applicationInstance.setHost(applicationInstance.getHost());
        this.applicationInstance.setPort(applicationInstance.getPort());
        this.applicationInstance.setStopURL(applicationInstance.getStopURL());
        this.applicationInstance.setLifetime(applicationInstance.getLifetime());
        this.applicationInstance.setDebugHost(applicationInstance.getDebugHost());
        this.applicationInstance.setDebugPort(applicationInstance.getDebugPort());
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInstance getPayload() {
        return applicationInstance;
    }
}