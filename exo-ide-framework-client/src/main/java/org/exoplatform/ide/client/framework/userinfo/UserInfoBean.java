/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.userinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfoBean implements UserInfo
{

   public static final String DEFAULT_USER_NAME = "DefaultUser";

   private String name;

   private List<String> groups;

   private List<String> roles;

   public UserInfoBean()
   {
   }

   public UserInfoBean(String name)
   {
      this.name = name;
   }

   public UserInfoBean(String name, List<String> groups, List<String> roles)
   {
      this.name = name;
      this.groups = groups;
      this.roles = roles;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#getName()
    */
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#setName(java.lang.String)
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#getGroups()
    */
   public List<String> getGroups()
   {
      if (groups == null)
         groups = new ArrayList<String>();
      return groups;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#setGroups(java.util.List)
    */
   public void setGroups(List<String> groups)
   {
      this.groups = groups;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#getRoles()
    */
   public List<String> getRoles()
   {
      if (roles == null)
         roles = new ArrayList<String>();
      return roles;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.UserInfo#setRoles(java.util.List)
    */
   public void setRoles(List<String> roles)
   {
      this.roles = roles;
   }

}