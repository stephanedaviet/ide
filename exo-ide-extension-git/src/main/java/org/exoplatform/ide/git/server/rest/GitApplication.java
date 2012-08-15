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
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.vfs.server.RequestContextResolver;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitApplication.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class GitApplication extends Application
{
   private final Set<Object> singletons;
   private final Set<Class<?>> classes;

   public GitApplication()
   {
      classes = new HashSet<Class<?>>(2);
      classes.add(GitService.class);
      classes.add(RequestContextResolver.class);

      singletons = new HashSet<Object>(7);
      singletons.add(new GitExceptionMapper());
      singletons.add(new InfoPageWriter());
      singletons.add(new BranchListWriter());
      singletons.add(new TagListWriter());
      singletons.add(new RemoteListWriter());
      singletons.add(new CommitMessageWriter());
      singletons.add(new MergeResultWriter());
   }

   /** @see javax.ws.rs.core.Application#getClasses() */
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   /**
    * session
    *
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
