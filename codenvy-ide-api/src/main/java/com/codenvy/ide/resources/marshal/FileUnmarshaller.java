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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.resources.model.File;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;


/**
 * Unmarsheller for {@link File}
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class FileUnmarshaller implements Unmarshallable<File>
{

   private final File item;

   public FileUnmarshaller(File item)
   {

      this.item = item;

   }

   /**
    * @see com.codenvy.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         item.init(JSONParser.parseLenient(response.getText()).isObject());
      }
      catch (Exception exc)
      {
         String message = "Can't parse item " + response.getText();
         throw new UnmarshallerException(message, exc);
      }

   }

   @Override
   public File getPayload()
   {
      return this.item;
   }

}