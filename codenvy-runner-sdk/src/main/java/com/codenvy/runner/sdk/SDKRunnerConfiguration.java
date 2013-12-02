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
package com.codenvy.runner.sdk;

import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.dto.RunRequest;

/**
 * Configuration for running app by deploying it to application server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class SDKRunnerConfiguration extends RunnerConfiguration {
    private final boolean suspend;
    private final String  transport;

    public SDKRunnerConfiguration(int httpPort, int memory, int debugPort, boolean suspend,
                                  String transport, RunRequest runRequest) {
        super(memory, httpPort, debugPort, runRequest);
        this.suspend = suspend;
        this.transport = transport;
    }

    public boolean isDebugSuspend() {
        return suspend;
    }

    public String getDebugTransport() {
        return transport;
    }

    @Override
    public String toString() {
        return "RunnerConfiguration{" +
               "memory=" + getMemory() +
               ", port=" + getPort() +
               ", debugPort=" + getDebugPort() +
               ", debugSuspend=" + suspend +
               ", debugTransport=" + transport +
               ", request=" + getRequest() +
               '}';
    }
}