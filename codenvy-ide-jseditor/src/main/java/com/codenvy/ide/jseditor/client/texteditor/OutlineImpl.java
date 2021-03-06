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
package com.codenvy.ide.jseditor.client.texteditor;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.texteditor.outline.CodeBlock;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.api.texteditor.outline.OutlinePresenter;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.outline.CodeBlockDataAdapter;
import com.codenvy.ide.outline.OutlineImpl.OutlineView;
import com.codenvy.ide.outline.OutlineViewImpl;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler.CursorHandler;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.Tree.Listener;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


/**
 * Implementation of {@link OutlinePresenter} for the embedded editors.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineImpl implements OutlinePresenter {

    private final OutlineView view;

    private final EmbeddedTextEditorPartView editor;

    private final OutlineModel model;
    private final CodeBlockDataAdapter dataAdapter;

    private CodeBlock blockToSync;

    private boolean thisCursorMove;

    /**
     *
     */
    public OutlineImpl(Resources resources, OutlineModel model,
                       EmbeddedTextEditorPartView editor, EmbeddedTextEditor editorPresenter) {
        this.model = model;
        this.editor = editor;
        dataAdapter = new CodeBlockDataAdapter();
        view = new OutlineViewImpl(resources, dataAdapter, this.model.getRenderer());
        editorPresenter.addPropertyListener(new PropertyListener() {

            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                if (EditorPartPresenter.PROP_INPUT == propId) {
                    Log.debug(OutlineImpl.class, "Binding outline to the source part.");
                    bind();
                }
            }
        });
        this.model.setListener(new OutlineModel.OutlineModelListener() {

            @Override
            public void rootUpdated() {
                view.renderTree();
            }

            @Override
            public void rootChanged(CodeBlock newRoot) {
                view.rootChanged(newRoot);
            }

        });
    }

    /**
     *
     */
    private void bind() {
        view.setTreeEventHandler(new Listener<CodeBlock>() {

            @Override
            public void onNodeAction(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<CodeBlock> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<CodeBlock> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<CodeBlock> node, SignalEvent event) {
                thisCursorMove = true;
                final CodeBlock data = node.getData();
                final int offset = data.getOffset();
                editor.getCursorModel().setCursorPosition(offset);
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }

            @Override
            public void onKeyboard(KeyboardEvent event) {
            }
        });

        if (editor.getCursorModel() != null) {
            Log.debug(OutlineImpl.class, "Cursor model available, adding cursor handler");
            editor.getCursorModel().addCursorHandler(new CursorHandler() {

                @Override
                public void onCursorChange(int line, int column, boolean isExplicitChange) {
                    if (thisCursorMove) {
                        thisCursorMove = false;
                        return;
                    }
                    if (model.getRoot() == null) {
                        return;
                    }
                    final TextPosition position = editor.getEmbeddedDocument().getCursorPosition();
                    final int offset = editor.getEmbeddedDocument().getIndexFromPosition(position);
                    blockToSync = null;
                    Tree.iterateDfs(model.getRoot(), dataAdapter, new Tree.Visitor<CodeBlock>() {

                        @Override
                        public boolean shouldVisit(CodeBlock node) {
                            if (offset + 1 > node.getOffset() && offset - 1 < node.getOffset() + node.getLength()) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        @Override
                        public void visit(CodeBlock node, boolean willVisitChildren) {
                            blockToSync = node;
                        }

                    });
                    if (blockToSync != null) {
                        if (!CodeBlock.ROOT_TYPE.equals(blockToSync.getType())) {
                            view.selectAndExpand(blockToSync);
                            return;
                        }
                    }
                }
            });
        } else {
            Log.debug(OutlineImpl.class, "No cursor model !!");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

}
