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
package org.exoplatform.ide.security.oauth;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allow store and provide services which implementations of OAuthAuthenticator.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class OAuthAuthenticatorProvider
{
   private final Map<String, OAuthAuthenticator> authenticators;

   public OAuthAuthenticatorProvider(ExoContainerContext containerContext)
   {
      authenticators = new HashMap<String, OAuthAuthenticator>();
      ExoContainer container = containerContext.getContainer();
      List allAuth = container.getComponentInstancesOfType(OAuthAuthenticator.class);
      if (!(allAuth == null || allAuth.isEmpty()))
      {
         for (Object o : allAuth)
         {
            OAuthAuthenticator auth = (OAuthAuthenticator)o;
            authenticators.put(auth.getOAuthProvider(), auth);
         }
      }
   }

   /**
    * Get authentication service by name.
    *
    * @param oauthProviderName
    *    name of OAuth provider
    * @return OAuthAuthenticator instance or <code>null</code> if specified OAuth provider is not supported
    */
   public OAuthAuthenticator getAuthenticator(String oauthProviderName)
   {
      return authenticators.get(oauthProviderName);
   }
}
