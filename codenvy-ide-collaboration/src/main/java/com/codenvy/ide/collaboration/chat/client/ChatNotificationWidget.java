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
package com.codenvy.ide.collaboration.chat.client;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.collaboration.dto.UserDetails;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import elemental.html.DivElement;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ChatNotificationWidget extends Composite
{
   private HTML html;

   public ChatNotificationWidget(UserDetails user, String message)
   {
      html = new HTML();
      DivElement name = Elements.createDivElement(ChatExtension.resources.chatCss().notificationName());
      name.setInnerHTML(user.getDisplayName());
      DivElement mes = Elements.createDivElement();
      mes.setInnerHTML(message);
      html.getElement().appendChild((Node)name);
      html.getElement().appendChild((Node)mes);
      initWidget(html);
   }
}
