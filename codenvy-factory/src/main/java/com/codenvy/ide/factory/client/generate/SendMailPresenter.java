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
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.FactoryClientService;
import com.codenvy.ide.factory.client.FactoryExtension;
import com.codenvy.ide.factory.client.marshaller.UserProfileUnmarshaller;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Presenter to share Factory URL by e-mail.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: SendMailPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class SendMailPresenter implements SendMailHandler, ViewClosedHandler {

    public interface Display extends IsView {

        /**
         * Returns 'To' field.
         *
         * @return 'To' field
         */
        HasValue<String> getRecipientField();

        /**
         * Returns 'Message' field.
         *
         * @return 'Message' field
         */
        HasValue<String> getMessageField();

        /**
         * Returns the 'Send' button.
         *
         * @return 'Send' button
         */
        HasClickHandlers getSendButton();

        /**
         * Returns the 'Cancel' button.
         *
         * @return 'Cancel' button
         */
        HasClickHandlers getCancelButton();

        /** Give focus to the 'To' field. */
        void focusRecipientField();

        /**
         * Enable send button.
         *
         * @param enable
         *         true if enable, otherwise false.
         */
        void enableSendButton(boolean enable);

        /**
         * Returns 'Sender Name' field.
         *
         * @return 'Sender Name' field
         */
        HasValue<String> getSenderName();

        /**
         * Returns 'Sender Email' field.
         *
         * @return 'Sender Email' field
         */
        HasValue<String> getSenderEmail();
    }

    /** Display. */
    private Display display;

    public SendMailPresenter() {
        IDE.addHandler(SendMailEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getSendButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doSend();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getRecipientField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                display.enableSendButton(isCorrectFilled());
            }
        });

        display.getMessageField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                display.enableSendButton(isCorrectFilled());
            }
        });

        display.getSenderEmail().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableSendButton(isCorrectFilled());
            }
        });

        display.getSenderName().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableSendButton(isCorrectFilled());
            }
        });
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailHandler#onSendMail(com.codenvy.ide.factory.client.generate.SendMailEvent) */
    @Override
    public void onSendMail(final SendMailEvent event) {
        try {
            final String requestUrl = Utils.getAuthorizationContext() + "/private/organization/users?alias=" + IDE.user.getName();

            UserProfileUnmarshaller unmarshaller = new UserProfileUnmarshaller(new HashMap<String, String>());
            
            AsyncRequestCallback<Map<String, String>> callback = new AsyncRequestCallback<Map<String, String>>(unmarshaller) {
                @Override
                protected void onSuccess(Map<String, String> result) {                    
                    showPopup(event.getProjectName(), event.getFactoryUrl(), 
                               IDE.user.getName(), result.get("firstName"), result.get("lastName"));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    //TODO remove this stub
                    if (Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0 ||
                        Location.getHost().indexOf("127.0.0.1:8080") >= 0) {                        
                        showPopup(event.getProjectName(), event.getFactoryUrl(), "ide", "Vitaliy", "Guluy");
                        return;
                    }
                    
                    Dialogs.getInstance().showError(FactoryExtension.LOCALIZATION_CONSTANTS.sendMailErrorGettingProfile());
                }
            };

            AsyncRequest.build(RequestBuilder.GET, requestUrl)
                        .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
    
    private void showPopup(String projectName, String factoryURL, String userName, String firstName, String lastName) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }        
        
        //String firstAndLastName = result.get("firstName") + " " + result.get("lastName");
        String firstAndLastName = firstName + " " + lastName;
        display.getSenderEmail().setValue(userName);
        display.getSenderName().setValue(firstAndLastName);
        String messageTemplate = FactoryExtension.LOCALIZATION_CONSTANTS.sendMailFieldMessageEntry(projectName, factoryURL, firstAndLastName, userName);
        display.getMessageField().setValue(messageTemplate);
        display.focusRecipientField();        
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void doSend() {
        String recipient = display.getRecipientField().getValue();
        String message = display.getMessageField().getValue();
        try {
            FactoryClientService.getInstance().share(recipient, message, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.getInstance().closeView(display.asView().getId());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Checks if recipient and message fields are filled correctly.
     *
     * @return true if email is valid and message field isn't empty otherwise false.
     */
    private boolean isCorrectFilled() {
        return !display.getRecipientField().getValue().isEmpty() && !display.getMessageField().getValue().isEmpty() &&
               !display.getSenderEmail().getValue().isEmpty() && !display.getSenderName().getValue().isEmpty() &&
               display.getRecipientField().getValue().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                                              + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }
}
