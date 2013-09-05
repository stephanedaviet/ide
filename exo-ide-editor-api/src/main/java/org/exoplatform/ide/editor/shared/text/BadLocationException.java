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
package org.exoplatform.ide.editor.shared.text;

/**
 * Indicates the attempt to access a non-existing position. The attempt has been performed on a text store such as a document or
 * string.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 */
public class BadLocationException extends Exception {

    /**
     * Serial version UID for this class.
     * <p>
     * Note: This class is not intended to be serialized.
     * </p>
     *
     * @since 3.1
     */
    private static final long serialVersionUID = 3257281452776370224L;

    /** Creates a new bad location exception. */
    public BadLocationException() {
        super();
    }

    /**
     * Creates a new bad location exception.
     *
     * @param message
     *         the exception message
     */
    public BadLocationException(String message) {
        super(message);
    }
}