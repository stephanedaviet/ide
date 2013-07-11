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

import org.exoplatform.gwtframework.commons.xml.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Java class for representation_type complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="representation_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://research.sun.com/wadl/2006/10}doc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://research.sun.com/wadl/2006/10}param" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="element" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="mediaType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="profile" type="{http://research.sun.com/wadl/2006/10}uriList" />
 *       &lt;attribute name="status" type="{http://research.sun.com/wadl/2006/10}statusCodeList" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class RepresentationType {

    protected List<Doc> doc;

    protected List<Param> param;

    protected List<Object> any;

    protected QName element;

    protected String href;

    protected String id;

    protected String mediaType;

    protected List<String> profile;

    protected List<Long> status;

    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the doc property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the doc property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDoc().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Doc }
     */
    public List<Doc> getDoc() {
        if (doc == null) {
            doc = new ArrayList<Doc>();
        }
        return this.doc;
    }

    /**
     * Gets the value of the param property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Param }
     */
    public List<Param> getParam() {
        if (param == null) {
            param = new ArrayList<Param>();
        }
        return this.param;
    }

    /**
     * Gets the value of the any property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the element property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getElement() {
        return element;
    }

    /**
     * Sets the value of the element property.
     *
     * @param value
     *         allowed object is
     *         {@link QName }
     */
    public void setElement(QName value) {
        this.element = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the mediaType property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the value of the mediaType property.
     *
     * @param value
     *         allowed object is
     *         {@link String }
     */
    public void setMediaType(String value) {
        this.mediaType = value;
    }

    /**
     * Gets the value of the profile property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profile property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfile().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getProfile() {
        if (profile == null) {
            profile = new ArrayList<String>();
        }
        return this.profile;
    }

    /**
     * Gets the value of the status property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the status property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatus().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     */
    public List<Long> getStatus() {
        if (status == null) {
            status = new ArrayList<Long>();
        }
        return this.status;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * <p/>
     * <p/>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     * <p/>
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}