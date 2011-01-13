/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.discovery;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public abstract class DiscoveryService
{
   private static DiscoveryService instance;

   public static DiscoveryService getInstance()
   {
      return instance;
   }

   protected DiscoveryService()
   {
      instance = this;
   }

   public abstract void getEntryPoints();

   public abstract void getEntryPoints(String url);
   
   public abstract void getDefaultEntryPoint();
   
   /**
    * Get list of all deployed REST Services 
    */
   public abstract void getRestServices();

}
