/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.websocket.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when WebSocket message was received.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WSMessageReceivedEvent.java Jun 18, 2012 14:33:50 PM azatsarynnyy $
 * 
 */
public class WSMessageReceivedEvent extends GwtEvent<WSMessageReceivedHandler>
{
   /**
    * Received message.
    */
   private String message;

   public WSMessageReceivedEvent(String message)
   {
      this.message = message;
   }

   /**
    * Type, used to register event.
    */
   public static final GwtEvent.Type<WSMessageReceivedHandler> TYPE = new GwtEvent.Type<WSMessageReceivedHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<WSMessageReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(WSMessageReceivedHandler handler)
   {
      handler.onWSMessageReceived(this);
   }

   /**
    * Returns message.
    * 
    * @return message
    */
   public String getMessage()
   {
      return message;
   }

}