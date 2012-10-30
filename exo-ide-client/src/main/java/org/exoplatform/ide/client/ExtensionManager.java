/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.exoplatform.ide.extension.ExtensionDescription;
import org.exoplatform.ide.extension.ExtensionRegistry;
import org.exoplatform.ide.extension.css.CssExtension;
import org.exoplatform.ide.extension.demo.DemoExtension;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;

/**
 * {@link ExtensionManager} responsible for bringing up Extensions. It uses ExtensionRegistry to acquire 
 * Extension description and dependencies. 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class ExtensionManager
{

   @SuppressWarnings("rawtypes")
   private final JsonArray<Provider> extensions;

   private final ExtensionRegistry extensionRegistry;

   /**
    * 
    */
   @Inject
   public ExtensionManager(final ExtensionRegistry extensionRegistry, final Provider<DemoExtension> demoExt, final Provider<CssExtension> cssExt)
   {
      this.extensions = JsonCollections.createArray();
      this.extensionRegistry = extensionRegistry;
      this.extensions.add(demoExt);
      this.extensions.add(cssExt);
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("rawtypes")
   public void startExtensions()
   {
      for (Provider provider : extensions.asIterable())
      {
         // this will instantiate extension so it's get enabled
         // Order of startup is managed by GIN dependency injection framework
         provider.get();
      }
   }

   /**
   * {@inheritDoc}
   */
   public JsonStringMap<ExtensionDescription> getExtensionDescriptions()
   {
      return extensionRegistry.getExtensionDescriptions();
   }

}
