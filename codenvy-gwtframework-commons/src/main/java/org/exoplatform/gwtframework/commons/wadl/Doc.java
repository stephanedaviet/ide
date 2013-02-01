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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.08 at 11:22:32 AM EET 
//

package org.exoplatform.gwtframework.commons.wadl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.xml.QName;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

public class Doc
{

   protected List<Object> content;

   protected String title;

   protected String lang;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the content property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the content property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getContent().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link Object }
    * {@link String }
    * {@link Element }
    * 
    * 
    */
   public List<Object> getContent()
   {
      if (content == null)
      {
         content = new ArrayList<Object>();
      }
      return this.content;
   }

   /**
    * Gets the value of the title property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Sets the value of the title property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setTitle(String value)
   {
      this.title = value;
   }

   /**
    * Gets the value of the lang property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getLang()
   {
      return lang;
   }

   /**
    * Sets the value of the lang property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setLang(String value)
   {
      this.lang = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and 
    * the value is the string value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return
    *     always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
