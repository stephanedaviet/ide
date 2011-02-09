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
package org.exoplatform.ide.extension.groovy.server;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;

import java.security.Principal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Filter
public class DevelopmentResourceMethodFilter implements MethodInvokerFilter
{

   /**
    * {@inheritDoc}
    */
   public void accept(GenericMethodResource genericMethodResource)
   {
      String developer =
         genericMethodResource.getParentResource().getProperties().getFirst(GroovyScriptService.DEVELOPER_ID);
      if (developer != null)
      {
         ApplicationContext context = ApplicationContextImpl.getCurrent();
         SecurityContext security = context.getSecurityContext();
         Principal principal = security.getUserPrincipal();
         if (principal == null || !developer.equals(principal.getName()))
         {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(
               "There is no any resources matched to request path " + context.getPath()).type(MediaType.TEXT_PLAIN)
               .build());
         }
      }
   }

}
