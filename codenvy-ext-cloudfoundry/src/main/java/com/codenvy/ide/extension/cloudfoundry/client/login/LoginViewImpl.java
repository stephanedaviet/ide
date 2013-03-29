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
package com.codenvy.ide.extension.cloudfoundry.client.login;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link LoginView}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class LoginViewImpl extends DialogBox implements LoginView
{
   private static LoginViewImplUiBinder uiBinder = GWT.create(LoginViewImplUiBinder.class);

   @UiField
   TextBox email;

   @UiField
   PasswordTextBox password;

   @UiField
   Button btnLogIn;

   @UiField
   Button btnCancel;

   @UiField
   Label errorText;

   @UiField
   ListBox server;

   @UiField
   Label serverLabel;

   @UiField
   Label emailLabel;

   @UiField
   Label passwordLabel;

   private ActionDelegate delegate;

   interface LoginViewImplUiBinder extends UiBinder<Widget, LoginViewImpl>
   {
   }

   /**
    * Create view.
    * 
    * @param resources
    */
   @Inject
   protected LoginViewImpl(CloudFoundryResources resources)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Login to CloudFoundry");
      this.setWidget(widget);

      // adds styles to graphic components
      this.addStyleName(resources.cloudFoundryCss().login());
      serverLabel.addStyleName(resources.cloudFoundryCss().loginFont());
      emailLabel.addStyleName(resources.cloudFoundryCss().loginFont());
      passwordLabel.addStyleName(resources.cloudFoundryCss().loginFont());
      errorText.addStyleName(resources.cloudFoundryCss().loginErrorFont());

      // adds text with icon into button
      btnLogIn.setHTML(new Image(resources.okButton()) + " Log In");
      btnCancel.setHTML(new Image(resources.cancelButton()) + " Cancel");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getEmail()
   {
      return email.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEmail(String email)
   {
      this.email.setText(email);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getPassword()
   {
      return password.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPassword(String password)
   {
      this.password.setText(password);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getServer()
   {
      int serverIndex = server.getSelectedIndex();
      return serverIndex != -1 ? server.getItemText(serverIndex) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServer(String server)
   {
      int count = this.server.getItemCount();
      boolean isItemFound = false;

      // Looks up entered server into available list of servers
      int i = 0;
      while (i < count && !isItemFound)
      {
         String item = this.server.getItemText(i);
         isItemFound = item.equals(server);

         i++;
      }

      // If item was found then it will be shown otherwise do nothing
      if (isItemFound)
      {
         this.server.setSelectedIndex(i - 1);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setError(String message)
   {
      errorText.setText(message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableLoginButton(boolean enabled)
   {
      btnLogIn.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void focusInEmailField()
   {
      email.setFocus(true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServerValues(JsonArray<String> servers)
   {
      server.clear();
      for (int i = 0; i < servers.size(); i++)
      {
         server.addItem(servers.get(i));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.center();
      this.show();
   }

   @UiHandler("btnLogIn")
   void onBtnLogInClick(ClickEvent event)
   {
      delegate.onLogInClicked();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.onCancelClicked();
   }

   @UiHandler("email")
   void onEmailKeyUp(KeyUpEvent event)
   {
      delegate.onValueChanged();
   }

   @UiHandler("password")
   void onPasswordKeyUp(KeyUpEvent event)
   {
      delegate.onValueChanged();
   }
}