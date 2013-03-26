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
package org.exoplatform.ide.extension.cloudbees.server;

import com.cloudbees.api.AccountKeysResponse;
import com.cloudbees.api.BeesClient;
import org.exoplatform.ide.security.paas.Credential;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudBeesAuthenticator
{
   public void login(BeesClient beesClient,
                     String domain,
                     String email,
                     String password,
                     Credential credential) throws Exception
   {
      AccountKeysResponse r = beesClient.accountKeys(domain, email, password);
      credential.setAttribute("api_key", r.getKey());
      credential.setAttribute("secret", r.getSecret());
   }

   public void login(BeesClient beesClient, Credential credential) throws Exception
   {
      login(beesClient, getDomain(), getUsername(), getPassword(), credential);
   }

   // For test.

   public String getUsername()
   {
      return null;
   }

   public String getPassword()
   {
      return null;
   }

   public String getDomain()
   {
      return null;
   }
}
