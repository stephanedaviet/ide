/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Asynchronous CloudBees request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in this
 * case - showDialog method calls on {@link LoginPresenter}.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: HerokuAsyncRequestCallback.java Jun 24, 2011 2:27:50 PM vereshchaka $
 * @see CloudBeesRESTfulRequestCallback
 */
public abstract class CloudBeesAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    // TODO Login package have not been ported yet
//    private LoggedInHandler loggedIn;
//    private LoginCanceledHandler loginCanceled;
    private EventBus    eventBus;
    private ConsolePart console;

    public CloudBeesAsyncRequestCallback(Unmarshallable<T> unmarshaller, EventBus eventBus, ConsolePart console
//                                         LoggedInHandler loggedIn,
//                                         LoginCanceledHandler loginCanceled
                                        ) {
        super(unmarshaller);
//        this.loggedIn = loggedIn;
//        this.loginCanceled = loginCanceled;
        this.eventBus = eventBus;
        this.console = console;
    }

    public CloudBeesAsyncRequestCallback(EventBus eventBus, ConsolePart console
//            LoggedInHandler loggedIn,
//                                         LoginCanceledHandler loginCanceled
                                        ) {
//        this.loggedIn = loggedIn;
//        this.loginCanceled = loginCanceled;
        this.eventBus = eventBus;
        this.console = console;
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            // because of CloudBees returned not 401 status, but 500 status
            // and explanation, that user not autherised in text message,
            // that's why we must parse text message
            final String exceptionMsg = serverException.getMessage();
            if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                && exceptionMsg.contains("AuthFailure")) {
                // TODO execute method on login presenter
//                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled));
                return;
            }
        }
        console.print(exception.getMessage());
        eventBus.fireEvent(new ExceptionThrownEvent(exception));
    }
}