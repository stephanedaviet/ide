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
package com.codenvy.ide.texteditor.renderer;

import elemental.dom.Element;

import com.codenvy.ide.Resources;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.util.dom.Elements;

/**
 * The renderer for the debug line in the editor.
 *
 * @author Andrey Plotnikov
 */
public class DebugLineRenderer {
    private final Element lineHighlighter;
    private final Buffer  buffer;

    /**
     * Create renderer.
     *
     * @param buffer
     * @param res
     */
    public DebugLineRenderer(Buffer buffer, Resources res) {
        this.buffer = buffer;
        lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
        Elements.addClassName(res.workspaceEditorBufferCss().debugLine(), lineHighlighter);
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(-1), "px");
    }

    /** Update debug line. */
    public void showLine(int lineNumber) {
        if (!buffer.hasLineElement(lineHighlighter)) {
            buffer.addUnmanagedElement(lineHighlighter);
        }
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(lineNumber), "px");
    }

    /** Update debug line. */
    public void disableLine() {
        buffer.removeUnmanagedElement(lineHighlighter);
    }
}