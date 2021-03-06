/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.texteditor.api.codeassistant;

import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor;

/**
 * An <code>CodeAssistant</code> provides support on interactive content completion.
 * The content assistant is a {@link TextEditorPartView} add-on. Its
 * purpose is to propose, display, and insert completions of the content
 * of the text viewer's document at the viewer's cursor position. In addition
 * to handle completions, a content assistant can also be requested to provide
 * context information. Context information is shown in a tool tip like popup.
 * As it is not always possible to determine the exact context at a given
 * document offset, a content assistant displays the possible contexts and requests
 * the user to choose the one whose information should be displayed.
 * <p>
 * A content assistant has a list of {@link com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor}
 * objects each of which is registered for a  particular document content
 * type. The content assistant uses the processors to react on the request
 * of completing documents or presenting context information.
 * </p>
 * <p>
 * The interface can be implemented by clients. By default, clients use
 * <b>CodeAssistantImpl</b> as the standard
 * implementer of this interface.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface CodeAssistant {
    /**
     * Installs content assist support on the given text view.
     *
     * @param view
     *         the text view on which content assist will work
     */
    void install(TextEditorPartView view);

    /**
     * Uninstalls content assist support from the text view it has
     * previously be installed on.
     */
    void uninstall();

    /**
     * Shows all possible completions of the content at the display's cursor position.
     *
     * @return an optional error message if no proposals can be computed
     */
    String showPossibleCompletions();

    /**
     * Returns the code assist processor to be used for the given content type.
     *
     * @param contentType
     *         the type of the content for which this
     *         content assistant is to be requested
     * @return an instance code assist processor or
     * <code>null</code> if none exists for the specified content type
     */
    CodeAssistProcessor getCodeAssistProcessor(String contentType);
}
