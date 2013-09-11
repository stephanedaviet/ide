/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.java.jdi.server;

/**
 * Thrown by {@link Debugger} when request to add new breakpoint is invalid. Typically it means the location (class
 * name or line number) is invalid.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see Debugger#addBreakPoint(org.exoplatform.ide.extension.java.jdi.shared.BreakPoint)
 */
@SuppressWarnings("serial")
public final class InvalidBreakPointException extends DebuggerException {
    public InvalidBreakPointException(String message) {
        super(message);
    }

    public InvalidBreakPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
