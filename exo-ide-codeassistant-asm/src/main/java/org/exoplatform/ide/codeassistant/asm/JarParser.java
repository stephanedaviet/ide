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
package org.exoplatform.ide.codeassistant.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarParser
{

   public static List<TypeInfoBuilder> parse(File jarFile) throws IOException
   {
      /*
       * There are no way to predict entries order in jar, so, manifest will be added
       * to classes when all classes parsed successfully.
       */
      Manifest manifest = null;

      List<TypeInfoBuilder> classes = new ArrayList<TypeInfoBuilder>();
      ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
      try
      {
         ZipEntry entry = zip.getNextEntry();
         while (entry != null)
         {
            String name = entry.getName();
            if (name.endsWith(".class"))
            {
               classes.add(ClassParser.parse(zip));
            }
            else if (name.equalsIgnoreCase("MANIFEST.MF"))
            {
               manifest = new Manifest(zip);
            }
            entry = zip.getNextEntry();
         }
      }
      finally
      {
         zip.close();
      }

      for (TypeInfoBuilder builder : classes)
      {
         builder.addManifest(manifest);
      }
      return classes;
   }

}