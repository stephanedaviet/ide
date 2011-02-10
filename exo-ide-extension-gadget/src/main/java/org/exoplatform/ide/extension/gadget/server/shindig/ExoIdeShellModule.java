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
package org.exoplatform.ide.extension.gadget.server.shindig;

import org.apache.shindig.common.PropertiesModule;


/**
 * USE ONLY IN STANDALONE BUILD
 * 
 * 1. This class need for rewrite default location of 
 * shindig properties file. By default it locate in shindig-common.jar 
 * In this properties file described location of container.js file.
 * We can't use container.js from GateIn in standalone build because it use eXoGadgetServer context.
 * We use IDE-application and describe in in our container.js 
 * (see /exo-ide-module-gadget-server/src/main/resources/containers/default-ide/container.js).  
 * 
 * 2. It generate key file for gadget security (OAuth)
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ExoIdeShellModule extends PropertiesModule
{
   private static String DEFAULT_PROPERTIES = "ide.shell.shindig.properties";
   
   public ExoIdeShellModule()
   {
      super(DEFAULT_PROPERTIES);
      KeyCreator.createKeyFile();
   }

}
