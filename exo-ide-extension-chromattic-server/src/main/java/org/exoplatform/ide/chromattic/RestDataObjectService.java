/**
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
 *
 */

package org.exoplatform.ide.chromattic;

import org.chromattic.dataobject.CompilationSource;
import org.chromattic.dataobject.DataObjectService;
import org.chromattic.dataobject.NodeTypeFormat;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.impl.core.nodetype.NodeTypeManagerImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;

import java.io.InputStream;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/ide/chromattic/")
public class RestDataObjectService implements ResourceContainer
{

   /**
    * WebDav context uses for selecting jcr path of resource which is 
    * adressed via WebDav or IDE Virtual File System service (server)
    */
   public static final String WEBDAV_CONTEXT = "/ide-vfs-webdav/";

   private DataObjectService dataObjectService;

   private RepositoryService repositoryService;

   public RestDataObjectService(DataObjectService dataObjectService, RepositoryService repositoryService)
   {
      this.dataObjectService = dataObjectService;
      this.repositoryService = repositoryService;
   }

   /**
    * Compile Data Object Service and return Node Type definition.
    * 
    * @param uriInfo - UriInfo
    * @param location - Resource URL 
    * @return
    */
   @POST
   @Path("/generate-nodetype-definition")
   public String getNodeTypeDefinition(@Context UriInfo uriInfo, @QueryParam("dependecyPath") String dependecyPath,
      @QueryParam("do-location") String location, @QueryParam("nodeTypeFormat") NodeTypeFormat format)
      throws PathNotFoundException
   {
      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (location == null)
      {
         throw new IllegalArgumentException("You must specify location of the source script.");
      }
      if (jcrLocation == null)
      {
         throw new PathNotFoundException("Location of script " + location + " not found. ");
      }
      String repository = jcrLocation[0];
      String workspace = jcrLocation[1];

      String pp = jcrLocation[2];
      if (pp.startsWith("/"))
      {
         pp = pp.substring(1);
      }
      String path = "/" + jcrLocation[2];

      //TODO: 
      CompilationSource compilationSource;
      if (dependecyPath != null)
      {
         String[] depJcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), dependecyPath);
         if (depJcrLocation == null)
         {
            throw new PathNotFoundException("Location of dependency  " + dependecyPath + " not found. ");
         }
         compilationSource = new CompilationSource(depJcrLocation[0], depJcrLocation[1], depJcrLocation[3]);
      }
      else
      {
         compilationSource = new CompilationSource(repository, workspace, path);
      }
      return dataObjectService.generateSchema(format, compilationSource, path);
   }

   @POST
   @Path("/register-nodetype/{format}/{alreadyExistsBehaviour}")
   public void registerNodeType(@PathParam("format") NodeTypeFormat format,
      @PathParam("alreadyExistsBehaviour") Integer alreadyExistsBehaviour, InputStream nodeTypeDefinition)
      throws RepositoryException, RepositoryConfigurationException

   {
      NodeTypeManagerImpl nodeTypeManager =
         (NodeTypeManagerImpl)repositoryService.getDefaultRepository().getNodeTypeManager();
      switch (format)
      {
         case EXO :
            nodeTypeManager.registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour,
               NodeTypeDataManager.TEXT_XML);
            break;
         case CND :
            nodeTypeManager.registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour,
               NodeTypeDataManager.TEXT_X_JCR_CND);
            break;
         default :
            throw new RepositoryException("Unsupported content type:" + format.name());
      }

   }

   /**
    * @param baseUri base URI
    * @param location location of groovy script
    * @return array of {@link String}, which elements contain repository name, workspace name and 
    * path the path to JCR node that contains groovy script to be deployed
    */
   private String[] parseJcrLocation(String baseUri, String location)
   {
      baseUri += WEBDAV_CONTEXT;
      if (!location.startsWith(baseUri))
      {
         return null;
      }

      String[] elements = new String[3];
      location = location.substring(baseUri.length());
      elements[0] = location.substring(0, location.indexOf('/'));
      location = location.substring(location.indexOf('/') + 1);
      elements[1] = location.substring(0, location.indexOf('/'));
      elements[2] = location.substring(location.indexOf('/') + 1);
      return elements;
   }

}
