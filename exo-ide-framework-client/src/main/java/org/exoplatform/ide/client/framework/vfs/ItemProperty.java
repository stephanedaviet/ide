/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.vfs;

import org.exoplatform.gwtframework.commons.xml.QName;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ItemProperty
{

   public interface Namespace
   {

      static final String DAV = "DAV:";

      static final String JCR = "http://www.jcp.org/jcr/1.0";

      static final String EXO = "http://www.exoplatform.com/jcr/exo/1.0";

   }

   /*
    * WebDAV properties
    */

   public static final QName DISPLAYNAME = new QName("displayname", Namespace.DAV);

   public static final QName CREATIONDATE = new QName("creationdate", Namespace.DAV);

   public static final QName GETCONTENTTYPE = new QName("getcontenttype", Namespace.DAV);

   public static final QName GETLASTMODIFIED = new QName("getlastmodified", Namespace.DAV);

   public static final QName GETCONTENTLENGTH = new QName("getcontentlength", Namespace.DAV);

   public static final QName RESOURCETYPE = new QName("resourcetype", Namespace.DAV);

   public static final QName SUCCESSOR_SET = new QName("successor-set", Namespace.DAV);

   public static final QName PREDECESSOR_SET = new QName("predecessor-set", Namespace.DAV);

   public static final QName OWNER = new QName("owner", Namespace.DAV);
   
   public static final QName ISVERSIONED = new QName("isversioned", Namespace.DAV);
   
   public static final QName CHECKEDOUT = new QName("checked-out", Namespace.DAV);
   
   public static final QName CHECKEDIN = new QName("checked-in", Namespace.DAV);

   /*
    * JCR_PROPERTIES
    */

   public static final QName JCR_CONTENT = new QName("content", Namespace.JCR);

   public static final QName JCR_NODETYPE = new QName("nodeType", Namespace.JCR);

   public static final QName JCR_PRIMARYTYPE = new QName("primaryType", Namespace.JCR);

   public static final QName JCR_ISCHECKEDOUT = new QName("isCheckedOut", Namespace.JCR);

   public static final QName JCR_LOCKOWNER = new QName("lockOwner", Namespace.JCR);

   /*
    * EXO PROPERTIES
    */

   public static final QName EXO_AUTOLOAD = new QName("autoload", Namespace.EXO);

   /*
    * ACL Properties
    */

   public interface ACL
   {
      public static final QName ACL = new QName("acl", Namespace.DAV);

      public static final QName ACE = new QName("ace", Namespace.DAV);

      public static final QName PRINCIPAL = new QName("principal", Namespace.DAV);

      public static final QName GRANT = new QName("grant", Namespace.DAV);

      public static final QName PRIVILEGE = new QName("privilege", Namespace.DAV);

      public static final QName DENY = new QName("deny", Namespace.DAV);

      public static final QName HREF = new QName("href", "DAV:");

      public static final QName PROPERTY = new QName("property", "DAV:");

      public static final QName ALL = new QName("all", "DAV:");

      public static final QName READ = new QName("read", "DAV:");

      public static final QName WRITE = new QName("write", "DAV:");

   }
}
