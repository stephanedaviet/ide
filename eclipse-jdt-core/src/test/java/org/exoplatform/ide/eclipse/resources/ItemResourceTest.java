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
package org.exoplatform.ide.eclipse.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * General tests for {@link ItemResource}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ItemResourceTest.java Jan 8, 2013 12:54:02 PM azatsarynnyy $
 */
public class ItemResourceTest extends ResourcesBaseTest
{
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   @Test
   public void testGetName()
   {
      IPath originPath = new Path("/project/folder/file");
      IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
      assertEquals(originPath.lastSegment(), fileResource.getName());
   }

   @Test
   public void testGetFileExtension()
   {
      IPath originPath = new Path("/project/folder/file.bin");
      IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
      assertEquals(originPath.getFileExtension(), fileResource.getFileExtension());

      originPath = new Path("/project/folder/file.");
      fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
      assertEquals(originPath.getFileExtension(), "");

      originPath = new Path("/project/folder/file");
      fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
      assertNull(originPath.getFileExtension());
   }

   @Test
   public void testGetFullPath()
   {
      IPath originPath = new Path("/project/folder/file");
      IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
      assertEquals(originPath, fileResource.getFullPath());
   }

}
