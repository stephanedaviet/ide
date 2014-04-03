/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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

import com.codenvy.api.runner.RunnerException;
import com.google.inject.Inject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Path("sdk/{ws-id}")
public class UpdateService {
    @Inject
    private ApplicationUpdaterRegistry applicationUpdaterRegistry;

    @Path("update/{id}")
    @POST
    public void updateApplication(@PathParam("id") long id) throws RunnerException {
        ApplicationUpdater updater = applicationUpdaterRegistry.getUpdater(id);
        updater.update();
    }
}