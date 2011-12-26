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
package org.exoplatform.ide.codeassistant.jvm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Represent information about class method. Can be transform to JSON. <code>
 *  {
 *     "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 *     "genericExceptionTypes": [],
 *     "declaringClass": "java.lang.String",
 *     "name": "equals",
 *     "genericParameterTypes": "(java.lang.Object)",
 *     "modifiers": 1,
 *     "returnType": "boolean",
 *     "parameterTypes": "(Object)",
 *     "genericReturnType": "boolean"
 *   }
 * </code>
 * 
 */
public class MethodInfo extends RoutineInfo
{
   /**
    * Full Qualified Class Name that method return <code>java.lang.String</code>
    */
   private String genericReturnType;

   /**
    * Short Class Name that method return <code>String</code>
    */
   private String returnType;

   public MethodInfo()
   {
   }

   public MethodInfo(int modifiers, String name, String[] genericExceptionTypes, String genericParameterTypes,
      String parameterTypes, String generic, String declaringClass, String genericReturnType, String returnType)
   {
      super(modifiers, name, genericExceptionTypes, genericParameterTypes, parameterTypes, generic, declaringClass);
      this.genericReturnType = genericReturnType;
      this.returnType = returnType;
   }

   public String getGenericReturnType()
   {
      return genericReturnType;
   }

   public void setGenericReturnType(String genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }

   public void setReturnType(String returnType)
   {
      this.returnType = returnType;
   }

   public String getReturnType()
   {
      return returnType;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(genericReturnType);
      out.writeObject(returnType);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      genericReturnType = (String)in.readObject();
      returnType = (String)in.readObject();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.RoutineInfo#toString()
    */
   @Override
   public String toString()
   {
      return getGeneric();
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (genericReturnType == null ? 0 : genericReturnType.hashCode());
      result = prime * result + (returnType == null ? 0 : returnType.hashCode());
      return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!super.equals(obj))
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      MethodInfo other = (MethodInfo)obj;
      if (genericReturnType == null)
      {
         if (other.genericReturnType != null)
         {
            return false;
         }
      }
      else if (!genericReturnType.equals(other.genericReturnType))
      {
         return false;
      }
      if (returnType == null)
      {
         if (other.returnType != null)
         {
            return false;
         }
      }
      else if (!returnType.equals(other.returnType))
      {
         return false;
      }
      return true;
   }

}
