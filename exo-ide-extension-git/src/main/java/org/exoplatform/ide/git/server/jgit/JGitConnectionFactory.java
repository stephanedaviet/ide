/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitConnectionFactory.java 22811 2011-03-22 07:28:35Z andrew00x
 *          $
 */
@SuppressWarnings("serial")
public class JGitConnectionFactory extends GitConnectionFactory
{
   /*
    * XXX : Temporary solution to get access to remote Git Repository.
    * Need find appropriate place for it at least.
    * *****************************************************************
    * File GitCredentials.properties must be accessible to context
    * class-loader. File may contains: 1. username, password for
    * authentication over HTTP 2. ssh.host, ssh.passphrase for authentication
    * over SSH. If key is not protected by passphrase this parameters may be
    * omitted. Set up SSH keys described here
    * http://help.github.com/linux-set-up-git/.
    * 
    * Example of GitCredentials.properties file:
    * 
    * username=andrew00x
    * password=secret
    * ssh.host=git@github.com
    * ssh.passphrase=secret phrase
    */
   static
   {
      InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("GitCredentials.properties");
      if (ins != null)
      {
         Properties credentialProperties = new Properties();
         try
         {
            credentialProperties.load(ins);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         finally
         {
            try
            {
               ins.close();
            }
            catch (Exception e)
            {
            }
         }
         String username = credentialProperties.getProperty("username");
         String password = credentialProperties.getProperty("password");
         final String sshHost = credentialProperties.getProperty("ssh.host");
         final String sshPassphrase = credentialProperties.getProperty("ssh.passphrase");
         CredentialsProvider.setDefault(new CredentialsProviderImpl(username, password, new HashMap<String, String>() {
            {
               put(sshHost, sshPassphrase);
            }
         }));
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnectionFactory#getConnection(java.io.File,
    *      org.exoplatform.ide.git.shared.GitUser)
    */
   @Override
   public GitConnection getConnection(File workDir, GitUser user) throws GitException
   {
      return new JGitConnection(createRepository(workDir), user);
   }

   private static Repository createRepository(File workDir) throws GitException
   {
      try
      {
         return new FileRepository(new File(workDir, Constants.DOT_GIT));
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }
}
