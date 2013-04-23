/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.nodejs.client;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Handler for stop application request.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: StopApplicationStatusHandler.java Apr 18, 2013 4:12:17 PM vsvydenko $
 *
 */
public class StopApplicationStatusHandler implements RequestStatusHandler {
    private String projectName;

    public StopApplicationStatusHandler(String projectName) {
        this.projectName = projectName;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(NodeJsRuntimeExtension.NODEJS_LOCALIZATION.stoppingProjectMessage(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(NodeJsRuntimeExtension.NODEJS_LOCALIZATION.projectStoppedMessage(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable) */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, JobStatus.ERROR);
        job.setError(exception);
        IDE.fireEvent(new JobChangeEvent(job));
    }
}
