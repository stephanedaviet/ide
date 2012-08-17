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
package org.exoplatform.ide.security.openid;

import org.exoplatform.ide.security.shared.User;

/**
 * Represents an User authenticated with OpenId (through Google only at the moment).
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OpenIDUser implements User
{
   private final String id;
   // Temporary password generated by OpenIDAuthenticationService.
   private final String password;

   public OpenIDUser(String id, String password)
   {
      this.id = id;
      this.password = password;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public void setId(String id)
   {
      // prevent updating user id.
      throw new UnsupportedOperationException();
   }

   public String getPassword()
   {
      return password;
   }
}