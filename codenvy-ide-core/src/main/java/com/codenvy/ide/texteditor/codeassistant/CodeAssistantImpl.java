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
package com.codenvy.ide.texteditor.codeassistant;

import com.codenvy.ide.api.text.BadLocationException;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.text.TextUtilities;
import com.codenvy.ide.api.texteditor.CodeAssistCallback;
import com.codenvy.ide.api.texteditor.KeyListener;
import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.UndoManager;
import com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.api.texteditor.codeassistant.Completion;
import com.codenvy.ide.api.texteditor.codeassistant.CompletionProposal;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringMap.IterationCallback;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.Buffer.ScrollListener;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant;
import com.codenvy.ide.texteditor.codeassistant.AutocompleteBox.Events;
import com.codenvy.ide.texteditor.codeassistant.AutocompleteUiController.Resources;
import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.codenvy.ide.util.input.KeyCodeMap;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.input.SignalEvent.KeySignalType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CodeAssistantImpl implements CodeAssistant {

    private StringMap<CodeAssistProcessor> processors;

    private AutocompleteBox box;

    private TextEditorViewImpl textEditor;

    private String lastErrorMessage;

    private String partitioning;

    private Events events = new Events() {

        @Override
        public void onSelect(CompletionProposal proposal) {
            closeBox();
            applyProposal(proposal);
        }

        @Override
        public void onCancel() {
            closeBox();
        }
    };

    private KeyListener keyListener = new KeyListener() {

        @Override
        public boolean onKeyPress(SignalEvent event) {
            if (box.isShowing() && box.consumeKeySignal(new SignalEventEssence(event))) {
                return true;
            }
            if (box.isShowing()) {
                scheduleRequestCodeassistant();
                return false;
            }
            if (event.getKeySignalType() == KeySignalType.INPUT) {
                int letter = KeyCodeMap.getKeyFromEvent(event);
                if (computeAllAutoActivationTriggers().contains(String.valueOf((char)letter))) {
                    scheduleRequestCodeassistant();
                }
            }
            return false;

        }
    };

    /** A listener that dismisses the autocomplete box when the editor is scrolled. */
    private ScrollListener dismissingScrollListener = new ScrollListener() {

        @Override
        public void onScroll(Buffer buffer, int scrollTop) {
            closeBox();
        }
    };

    private Remover keyListenerRemover;

    //TODO inject this
    public static final Resources res = GWT.create(Resources.class);

    /**
     *
     */
    public CodeAssistantImpl() {
        processors = Collections.createStringMap();
        partitioning = Document.DEFAULT_PARTITIONING;
        res.defaultSimpleListCss().ensureInjected();
        res.autocompleteComponentCss().ensureInjected();
        res.popupCss().ensureInjected();
    }

    /** @param proposal */
    private void applyProposal(CompletionProposal proposal) {
        if (proposal == null)
            return;

        proposal.getCompletion(new CompletionProposal.CompletionCallback() {
            @Override
            public void onCompletion(Completion completion) {
                UndoManager undoManager = textEditor.getEditorDocumentMutator().getUndoManager();
                if (undoManager != null)
                    undoManager.beginCompoundChange();
                try {
                    completion.apply(textEditor.getDocument());
                    Region selection = completion.getSelection(textEditor.getDocument());
                    if (selection != null) {
                        textEditor.getSelection().selectAndReveal(selection.getOffset(), selection.getLength());
                    }
                } catch (Exception e) {
                    Log.error(getClass(), e);
                } finally {
                    if (undoManager != null)
                        undoManager.endCompoundChange();
                }
            }
        });

    }

    /**
     * Computes the sorted set of all auto activation trigger characters.
     *
     * @return the sorted set of all auto activation trigger characters
     */
    private String computeAllAutoActivationTriggers() {
        if (processors == null) {
            return ""; //$NON-NLS-1$
        }
        final StringBuffer buf = new StringBuffer(5);
        processors.iterate(new IterationCallback<CodeAssistProcessor>() {

            @Override
            public void onIteration(String key, CodeAssistProcessor value) {
                char[] triggers = value.getCompletionProposalAutoActivationCharacters();
                if (triggers != null)
                    buf.append(triggers);
            }
        });
        return buf.toString();
    }

    /** @see com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant#install(com.codenvy.ide.api.texteditor.TextEditorPartView) */
    @Override
    public void install(TextEditorPartView view) {
        this.textEditor = (TextEditorViewImpl)view;
        box = new AutocompleteUiController(textEditor, res);
        keyListenerRemover = view.getKeyListenerRegistrar().add(keyListener);
        box.setDelegate(events);
        textEditor.getBuffer().getScrollListenerRegistrar().add(dismissingScrollListener);
    }

    /** @see com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant#uninstall() */
    @Override
    public void uninstall() {
        if (keyListenerRemover != null)
            keyListenerRemover.remove();
    }

    private void scheduleRequestCodeassistant() {
        if (!box.isShowing()) {
            box.showLoader();
        }
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                int offset =
                        TextUtilities.getOffset(textEditor.getDocument(), textEditor.getSelection().getCursorLineNumber(),
                                                textEditor.getSelection().getCursorColumn());
                if (offset > 0) {
                    computeCompletionProposals(textEditor, offset, new CodeAssistCallback() {

                        @Override
                        public void proposalComputed(CompletionProposal[] proposals) {
                            if (!box.isShowing()) {
                                if (proposals != null && proposals.length == 1 && proposals[0].isAutoInsertable()) {
                                    applyProposal(proposals[0]);
                                    return;
                                }
                            }
                            box.positionAndShow(proposals);
                        }
                    });

                }
            }
        });
    }

    /** @see com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant#showPossibleCompletions() */
    @Override
    public String showPossibleCompletions() {
        scheduleRequestCodeassistant();

        return lastErrorMessage;
    }

    /**
     * Returns an array of completion proposals computed based on the specified document position.
     * The position is used to determine the appropriate content assist processor to invoke.
     *
     * @param view
     *         the view for which to compute the proposals
     * @param offset
     *         a document offset
     * @return an array of completion proposals or <code>null</code> if no proposals are possible
     */
    void computeCompletionProposals(TextEditorPartView view, int offset, CodeAssistCallback callback) {
        lastErrorMessage = null;

        CodeAssistProcessor p = getProcessor(view, offset);
        if (p != null) {
            p.computeCompletionProposals(view, offset, callback);
            lastErrorMessage = p.getErrorMessage();
            if (lastErrorMessage != null) {
                box.showError(lastErrorMessage);
            }
        }
    }

    /**
     * Returns the code assist processor for the content type of the specified document position.
     *
     * @param view
     *         the text view
     * @param offset
     *         a offset within the document
     * @return a code-assist processor or <code>null</code> if none exists
     */
    private CodeAssistProcessor getProcessor(TextEditorPartView view, int offset) {
        try {
            Document document = view.getDocument();
            String type = TextUtilities.getContentType(document, partitioning, offset, true);
            return getCodeAssistProcessor(type);

        } catch (BadLocationException x) {
        }

        return null;
    }

    protected void closeBox() {
        box.dismiss();
    }

    /** @see com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant#getCodeAssistProcessor(java.lang.String) */
    @Override
    public CodeAssistProcessor getCodeAssistProcessor(String contentType) {
        return processors.get(contentType);
    }

    public void setCodeAssistantProcessor(String contentType, CodeAssistProcessor processor) {
        processors.put(contentType, processor);
    }

}
