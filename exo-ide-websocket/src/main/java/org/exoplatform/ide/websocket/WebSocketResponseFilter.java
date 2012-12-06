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
package org.exoplatform.ide.websocket;

import org.everrest.core.Filter;
import org.everrest.core.GenericContainerResponse;
import org.everrest.core.ResponseFilter;
import org.exoplatform.services.security.ConversationState;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Filter
public class WebSocketResponseFilter implements ResponseFilter
{
   @Override
   public void doFilter(GenericContainerResponse response)
   {
      MultivaluedMap<String, Object> headers = response.getHttpHeaders();
      if (headers != null && headers.containsKey("x-everrest-protocol") && headers.containsValue("websocket"))
      {
         ConversationState.setCurrent(null);
      }
   }
}
