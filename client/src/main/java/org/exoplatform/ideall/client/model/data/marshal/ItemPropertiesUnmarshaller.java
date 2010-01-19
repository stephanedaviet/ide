/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.data.marshal;

import org.exoplatform.gwt.commons.rest.Unmarshallable;
import org.exoplatform.gwt.commons.webdav.PropfindResponse;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwt.commons.xml.QName;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.property.ItemProperty;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemPropertiesUnmarshaller implements Unmarshallable
{

   private Item item;

   public ItemPropertiesUnmarshaller(Item item)
   {
      this.item = item;
   }

   public void unmarshal(String body)
   {
      item.getProperties().clear();

      // TODO to fix bug with the Internet Explorer XML Parser, when parsing node with property b:dt="dateTime.rfc1123" (http://markmail.org/message/ai2wypfkbhazhrdp)
      body = body.replace(" b:dt=\"dateTime.rfc1123\"", "");

      PropfindResponse propfindResponse = PropfindResponse.parse(body);
      Resource resource = propfindResponse.getResource();

      item.getProperties().clear();
      item.getProperties().addAll(resource.getProperties());

      if (item instanceof File)
      {
         String contentType = getProperty(item, ItemProperty.GETCONTENTTYPE).getValue();
         ((File)item).setContentType(contentType);
         String jcrNodeType = NodeTypeUtil.getContentNodeType(contentType);
         ((File)item).setJcrContentNodeType(jcrNodeType);
         String icon = ImageUtil.getIcon(contentType);
         item.setIcon(icon);
      }

   }

   private Property getProperty(Item item, QName name)
   {
      for (Property property : item.getProperties())
      {
         if (property.getName().equals(name))
         {
            return property;
         }
      }

      return null;
   }

}
