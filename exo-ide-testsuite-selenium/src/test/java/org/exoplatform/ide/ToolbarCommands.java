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
package org.exoplatform.ide;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public interface ToolbarCommands
{
   public interface Main
   {

   }

   public interface Editor
   {
      public static final String FIND_REPLACE = "Find/Replace...";
      
      public static final String UNDO = "Undo Typing";
      
      public static final String REDO = "Redo Typing";
   }
   
   public interface File
   {
      public static final String SAVE = "Save";
      
      public static final String SAVE_AS = "Save As...";
      
      public static final String DELETE = "Delete Item(s)...";
      
      public static final String REFRESH = "Refresh Selected Folder";
   }
   
   public interface View
   {
      public static final String SHOW_OUTLINE = "Show Outline";
      
      public static final String HIDE_OUTLINE = "Hide Outline";
      
      public static final String SHOW_PROPERTIES = "Show Properties";
   }
   
   public interface Run
   {
      public static final String SHOW_PREVIEW = "Show Preview";
      
      public static final String DEPLOY_GADGET = "Deploy Gadget to GateIn";
      
      public static final String UNDEPLOY_GADGET = "UnDeploy Gadget from GateIn";

   }
}
