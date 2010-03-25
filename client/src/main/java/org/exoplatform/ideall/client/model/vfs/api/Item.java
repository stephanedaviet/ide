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
package org.exoplatform.ideall.client.model.vfs.api;

import java.util.ArrayList;
import java.util.Collection;

import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.xml.QName;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class Item
{

   private String href;

   private Collection<Property> properties = new ArrayList<Property>();

   private boolean propertiesChanged = false;

   private String icon;

   protected Item(String href)
   {
      this.href = href;
   }

   //TODO remove this method !!!
   public String getPath() {
      return null;
   }
   
   public String getHref()
   {
      return href;
   }

   /**
    * @return the propertiesChanged
    */
   public boolean isPropertiesChanged()
   {
      return propertiesChanged;
   }

   /**
    * @param propertiesChanged the propertiesChanged to set
    */
   public void setPropertiesChanged(boolean propertiesChanged)
   {
      this.propertiesChanged = propertiesChanged;
   }

   /**
    * @return the properties
    */
   public Collection<Property> getProperties()
   {
      return properties;
   }
   
   public Property getProperty(QName propertyName) {
      for (Property property : properties) {
         if (propertyName.equals(property.getName())) {
            return property;
         }
      }
      
      return null;
   }

   public String getName()
   {
      String name = href;
      if (name.endsWith("/"))
      {
         name = name.substring(0, name.length() - 1);
      }
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
   }

   /**
    * @return the icon
    */
   public String getIcon()
   {
      return icon;
   }

   /**
    * @param icon the icon to set
    */
   public void setIcon(String icon)
   {
      this.icon = icon;
   }

}
