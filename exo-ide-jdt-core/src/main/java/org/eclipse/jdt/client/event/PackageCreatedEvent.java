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
package org.eclipse.jdt.client.event;

import org.exoplatform.ide.vfs.client.model.FolderModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class PackageCreatedEvent extends GwtEvent<PackageCreatedHandler>
{

   public static final Type<PackageCreatedHandler> TYPE = new Type<PackageCreatedHandler>();

   private final FolderModel parentFolder;

   private final String pack;

   /**
    * @param pack
    * @param parentFolder
    */
   public PackageCreatedEvent(String pack, FolderModel parentFolder)
   {
      this.pack = pack;
      this.parentFolder = parentFolder;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<PackageCreatedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(PackageCreatedHandler handler)
   {
      handler.onPackageCreated(this);
   }

   /**
    * @return the parentFolder
    */
   public FolderModel getParentFolder()
   {
      return parentFolder;
   }

   /**
    * @return the pack
    */
   public String getPack()
   {
      return pack;
   }

}
