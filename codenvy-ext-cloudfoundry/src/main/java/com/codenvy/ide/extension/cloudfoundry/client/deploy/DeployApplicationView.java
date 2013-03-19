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
package com.codenvy.ide.extension.cloudfoundry.client.deploy;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.view.View;
import com.google.gwt.user.client.ui.Composite;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DeployApplicationView extends View<DeployApplicationView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onNameFieldChanged();

      public void onUrlFieldChanged();

      public void onServerFieldChanged();
   }

   public String getName();

   public void setName(String name);

   public String getUrl();

   public void setUrl(String url);

   public String getServer();

   public void setServer(String server);

   /**
    * Set the list of servers to ServerSelectField.
    * 
    * @param servers
    */
   void setServerValues(JsonArray<String> servers);

   public Composite getView();
}