/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide;

import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;

import java.io.IOException;
import java.net.URL;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class VirtualFileSystemUtils
{
   
   
   
   private static HTTPConnection getConnection(URL url) throws ProtocolNotSuppException
   {
      HTTPConnection connection = new HTTPConnection(url);
      connection.removeModule(CookieModule.class);
      connection.addBasicAuthorization(TestConstants.REALM_GATEIN_DOMAIN, TestConstants.USER, TestConstants.PASSWD);
      return connection;
   }
   
   public static int put(String filePath, String mimeType, String contentNodeType, String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = getConnection(url);
      String data = Utils.readFileAsString(filePath);
      NVPair[] headers = new NVPair[3];
      headers[0] = new NVPair(HTTPHeader.CONTENT_TYPE, mimeType);
      headers[1] = new NVPair(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length()));
      headers[2] = new NVPair(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
      HTTPResponse response = connection.Put(url.getFile(), data, headers);
      return response.getStatusCode();
   }
   
   
   public static int put(byte[] data, String mimeType, String contentNodeType, String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = getConnection(url);
      NVPair[] headers = new NVPair[3];
      headers[0] = new NVPair(HTTPHeader.CONTENT_TYPE, mimeType);
      headers[1] = new NVPair(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length));
      headers[2] = new NVPair(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
      HTTPResponse response = connection.Put(url.getFile(), data, headers);
      return response.getStatusCode();
   }
   
   
   /**
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int put(byte[] data, String mimeType, String storageUrl) throws IOException, ModuleException
   {
      return put(data, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }
   /**
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int put(String filePath, String mimeType, String storageUrl) throws IOException, ModuleException
   {
      return put(filePath, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }
   
   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int delete(String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = getConnection(url);
      HTTPResponse response = connection.Delete(url.getFile());
      return response.getStatusCode();
   }
   
   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static HTTPResponse get(String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = getConnection(url);
      HTTPResponse response = connection.Get(url.getFile());
      return response;
   }
   
   
   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int mkcol(String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = getConnection(url);
      HTTPResponse response = connection.MkCol(url.getFile());
      return response.getStatusCode();
   }
}
