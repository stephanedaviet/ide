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
package org.exoplatform.ide.extension.googleappengine.server.oauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthAuthenticator
{
   private static final List<String> SCOPE = Collections.singletonList("https://www.googleapis.com/auth/appengine.admin");

   private final AuthorizationCodeFlow flow;
   private final String redirectUri;

   private static GoogleClientSecrets loadClientSecrets() throws IOException
   {
      InputStream secrets = Thread.currentThread().getContextClassLoader().getResourceAsStream("client_secrets.json");
      if (secrets != null)
      {
         return GoogleClientSecrets.load(new JacksonFactory(), secrets);
      }
      throw new IOException("Cannot load client secrets. File 'client_secrets.json' not found. ");
   }

   public OAuthAuthenticator() throws IOException
   {
      this(new MemoryCredentialStore(), loadClientSecrets());
   }

   protected OAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      this.flow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(), clientSecrets,
         SCOPE).setCredentialStore(credentialStore).build();
      List<String> redirectUris = clientSecrets.getDetails().getRedirectUris();
      if (redirectUris == null || redirectUris.isEmpty())
      {
         throw new RuntimeException("Redirect URI not found. ");
      }
      this.redirectUri = redirectUris.get(0);
   }

   /**
    * Get oauth token.
    *
    * @param userId
    *    user
    * @return oauth token or <code>null</code>
    * @throws IOException
    *    if i/o error occurs when try to refresh expired oauth token
    */
   public final String getToken(String userId) throws IOException
   {
      Credential credential = flow.loadCredential(userId);
      if (credential != null)
      {
         Long expirationTime = credential.getExpiresInSeconds();
         if (expirationTime != null && expirationTime < 0)
         {
            credential.refreshToken();
         }
         return credential.getAccessToken();
      }
      return null;
   }

   /**
    * Invalidate OAuth token for specified user.
    *
    * @param userId
    *    user
    * @return <code>true</code> if OAuth token invalidated and <code>false</code> otherwise, e.g. if user does not have
    *         token yet
    */
   public final boolean invalidateToken(String userId)
   {
      Credential credential = flow.loadCredential(userId);
      if (credential != null)
      {
         flow.getCredentialStore().delete(userId, credential);
         return true;
      }
      return false;
   }

   /**
    * Create authentication URL.
    *
    * @return URL for authentication
    */
   public final String getAuthenticateUri(String userId)
   {
      AuthorizationCodeRequestUrl url = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
      StringBuilder state = new StringBuilder();
      addState(state);
      if (state.length() > 0)
      {
         state.append('&');
      }
      state.append("userId=");
      state.append(userId);
      url.setState(state.toString());
      return url.build();
   }

   protected void addState(StringBuilder state)
   {
   }

   /**
    * Process callback request.
    *
    * @param requestUri
    *    request URI. URI should contain authorization code generated by authorization server
    * @throws OAuthAuthenticationException
    *    if authentication failed or <code>requestUri</code> does not contain required parameters, e.g. 'code'
    */
   public final void callback(String requestUri) throws OAuthAuthenticationException
   {
      AuthorizationCodeResponseUrl authorizationCodeResponseUrl = new AuthorizationCodeResponseUrl(requestUri);

      final String error = authorizationCodeResponseUrl.getError();
      if (error != null)
      {
         throw new OAuthAuthenticationException("Authentication failed: " + error);
      }

      final String code = authorizationCodeResponseUrl.getCode();
      if (code == null)
      {
         throw new OAuthAuthenticationException("Missing authorization code. ");
      }

      try
      {
         String state = authorizationCodeResponseUrl.getState();
         String userId = null;
         if (!(state == null || state.isEmpty()))
         {
            String decoded = URLDecoder.decode(state, "UTF-8");
            String[] items = decoded.split("&");
            for (String str : items)
            {
               if (str.startsWith("userId="))
               {
                  userId = str.substring(7, str.length());
               }
            }
         }
         if (userId == null)
         {
            throw new OAuthAuthenticationException("Missing user ID. ");
         }
         TokenResponse resp = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
         flow.createAndStoreCredential(resp, userId);
      }
      catch (IOException ioe)
      {
         throw new OAuthAuthenticationException(ioe.getMessage());
      }
   }
}
