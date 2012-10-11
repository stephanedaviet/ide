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
package org.exoplatform.ide.extension.java.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class BuildTask extends TimerTask
{

   private static final Log LOG = ExoLogger.getLogger(BuildTask.class);

   /**
    * 
    */
   protected final String dependencyList;

   /**
    * 
    */
   protected final Item project;

   /**
    * 
    */
   protected final String copyId;

   protected final BuilderClient client;

   protected final CodeAssistantStorageClient storageClient;

   /**
    * @param dependencyList
    * @param project
    * @param copyId
    * @param client
    * @param storageClient
    */
   public BuildTask(String dependencyList, Item project, String copyId, BuilderClient client,
      CodeAssistantStorageClient storageClient)
   {
      this.dependencyList = dependencyList;
      this.project = project;
      this.copyId = copyId;
      this.client = client;
      this.storageClient = storageClient;
   }

   @Override
   public void run()
   {
      try
      {
         String status = client.status(copyId);
         JsonParser parser = new JsonParser();
         parser.parse(new ByteArrayInputStream(status.getBytes("UTF-8")));
         BuildStatusBean buildStatus = ObjectBuilder.createObject(BuildStatusBean.class, parser.getJsonObject());
         if (Status.IN_PROGRESS != buildStatus.getStatus())
         {
            cancel();
            if (Status.SUCCESSFUL == buildStatus.getStatus())
            {
               
               if (buildStatus.getDownloadUrl() != null && !buildStatus.getDownloadUrl().isEmpty())
               {
                  new URL(buildStatus.getDownloadUrl());//validation download URL 
                  buildSuccess(buildStatus.getDownloadUrl());
               }
            }
            else
               LOG.warn("Build failed, exit code: " + buildStatus.getExitCode() + ", message: "
                  + buildStatus.getError());
         }

      }
      catch (BuilderException e)
      {
         cancel();
         LOG.error("Copy dependency of the '" + project.getPath() + " failed", e);
      }
      catch (UnsupportedEncodingException e)
      {
         cancel();
         LOG.error("Copy dependency of the '" + project.getPath() + " failed", e);
      }
      catch (JsonException e)
      {
         cancel();
         LOG.error("Copy dependency of the '" + project.getPath() + " failed. Can't parse server response", e);
      }
      catch (MalformedURLException e)
      {
         cancel();
         LOG.error("Copy dependency of the '" + project.getPath() + " failed. Get not valid download URL", e);
      }
      catch (IOException e)
      {
         cancel();
         LOG.error("Copy dependency of the '" + project.getPath() + " failed", e);
      }
   }

   protected abstract void buildSuccess(String downloadUrl) throws IOException;

}