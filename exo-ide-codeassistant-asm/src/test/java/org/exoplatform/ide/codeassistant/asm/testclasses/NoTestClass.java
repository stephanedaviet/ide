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
package org.exoplatform.ide.codeassistant.asm.testclasses;

public class NoTestClass extends NoTestSuper implements NoTestInterface, NoTestInterface2
{

   private int a;

   public final String b = "b";

   public static final Double c = 5.0;

   protected double d;

   public NoTestClass(int a, String b) throws ClassNotFoundException
   {

   }

   @Override
   public int method3(double a, int b, char c, float[][][] d, String[] e)
   {
      return 0;
   }

   @Override
   public void method4(String a, Boolean b, boolean c, int[][][][][] d)
   {
   }

   @Override
   public int method1()
   {
      return 0;
   }

   @Override
   public int method2(int a)
   {
      return 0;
   }

   @Override
   protected void method0(int a)
   {
   }

}