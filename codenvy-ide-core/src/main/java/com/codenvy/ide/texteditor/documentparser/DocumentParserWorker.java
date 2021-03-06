// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.texteditor.documentparser;

import com.codenvy.ide.api.texteditor.parser.Parser;
import com.codenvy.ide.api.texteditor.parser.State;
import com.codenvy.ide.api.texteditor.parser.Stream;
import com.codenvy.ide.api.texteditor.parser.Token;
import com.codenvy.ide.api.texteditor.parser.TokenType;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.Position;
import com.codenvy.ide.text.store.TaggableLine;
import com.codenvy.ide.text.store.anchor.Anchor;
import com.codenvy.ide.text.store.anchor.AnchorManager;
import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.loging.Log;


/**
 * Worker that performs the actual parsing of the document by delegating to
 * CodeMirror.
 */
class DocumentParserWorker {

    private static final int LINE_LENGTH_LIMIT = 1000;

    private static class ParserException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        ParserException(Throwable t) {
            super(t);
        }
    }

    private interface ParsedTokensRecipient {
        void onTokensParsed(Line line, int lineNumber, Array<Token> tokens);
    }

    private static final String LINE_TAG_END_OF_LINE_PARSER_STATE_SNAPSHOT = DocumentParserWorker.class.getName()
                                                                             + ".endOfLineParserStateSnapshot";

    private final Parser codeMirrorParser;

    private final DocumentParser documentParser;

    private final ParsedTokensRecipient documentParserDispatcher = new ParsedTokensRecipient() {
        @Override
        public void onTokensParsed(Line line, int lineNumber, Array<Token> tokens) {
            documentParser.dispatch(line, lineNumber, tokens);
        }
    };

    DocumentParserWorker(DocumentParser documentParser, Parser codeMirrorParser) {
        this.documentParser = documentParser;
        this.codeMirrorParser = codeMirrorParser;
    }

    /**
     * Parses the given lines and updates the parser position {@code anchorToUpdate}.
     *
     * @return {@code true} is parsing should continue
     */
    boolean parse(Line line, int lineNumber, int numLinesToProcess, Anchor anchorToUpdate) {
        return parseImplCm2(line, lineNumber, numLinesToProcess, anchorToUpdate, documentParserDispatcher);
    }

    /**
     * @param lineNumber
     *         the line number of {@code line}. This can be -1 if
     *         {@code anchorToUpdate} is null
     * @param anchorToUpdate
     *         the optional anchor that this method will update
     */
    private boolean parseImplCm2(Line line, int lineNumber, int numLinesToProcess, Anchor anchorToUpdate,
                                 ParsedTokensRecipient tokensRecipient) {

        State parserState = loadParserStateForBeginningOfLine(line);
        if (parserState == null) {
            return false;
        }

        Line previousLine = line.getPreviousLine();

        for (int numLinesProcessed = 0; line != null && numLinesProcessed < numLinesToProcess; ) {
            State stateToSave = parserState;
            if (line.getText().length() > LINE_LENGTH_LIMIT) {
                // Save the initial state instead of state at the end of line.
                stateToSave = parserState.copy(codeMirrorParser);
            }

            Array<Token> tokens;
            try {
                tokens = parseLine(parserState, line.getText());
            } catch (ParserException e) {
                Log.error(getClass(), "Could not parse line:", line, e);
                return false;
            }

            // Restore the initial line state if it was preserved.
            parserState = stateToSave;
            saveEndOfLineParserState(line, parserState);
            tokensRecipient.onTokensParsed(line, lineNumber, tokens);

            previousLine = line;
            line = line.getNextLine();
            numLinesProcessed++;
            if (lineNumber != -1) {
                lineNumber++;
            }
        }

        if (anchorToUpdate != null) {
            if (lineNumber == -1) {
                throw new IllegalArgumentException("lineNumber cannot be -1 if anchorToUpdate is given");
            }

            if (line != null) {
                line.getDocument().getAnchorManager()
                    .moveAnchor(anchorToUpdate, line, lineNumber, AnchorManager.IGNORE_COLUMN);
            } else {
                previousLine.getDocument().getAnchorManager()
                            .moveAnchor(anchorToUpdate, previousLine, lineNumber - 1, AnchorManager.IGNORE_COLUMN);
            }
        }

        return line != null;
    }

    /**
     * @return the parsed tokens, or {@code null} if the line could not be parsed
     * because there isn't a snapshot and it's not the first line
     */
    Array<Token> parseLine(Line line) {
        class TokensRecipient implements ParsedTokensRecipient {
            Array<Token> tokens;

            @Override
            public void onTokensParsed(Line line, int lineNumber, Array<Token> tokens) {
                this.tokens = tokens;
            }
        }

        TokensRecipient tokensRecipient = new TokensRecipient();
        parseImplCm2(line, -1, 1, null, tokensRecipient);
        return tokensRecipient.tokens;
    }

    int getIndentation(Line line) {
        State stateBefore = loadParserStateForBeginningOfLine(line);
        String textAfter = line.getText();
        textAfter = textAfter.substring(StringUtils.lengthOfStartingWhitespace(textAfter));
        return codeMirrorParser.indent(stateBefore, textAfter);
    }

    /**
     * Create a copy of a parser state corresponding to the beginning of
     * the given line.
     * <p/>
     * <p>Actually, the state we are looking for is a final state of
     * parser after processing the previous line, since codemirror parsers are
     * line-based.
     * <p/>
     * <p>Parser state for the first line is a default parser state.
     * <p/>
     * <p>We always return a copy to avoid changes to persisted state.
     *
     * @return copy of corresponding parser state, or {@code null} if the state
     * if not known yet (previous line wasn't parsed).
     */
    private <T extends State> T loadParserStateForBeginningOfLine(TaggableLine line) {
        State state;
        if (line.isFirstLine()) {
            state = codeMirrorParser.defaultState();
        } else {
            state = line.getPreviousLine().getTag(LINE_TAG_END_OF_LINE_PARSER_STATE_SNAPSHOT);
            state = (state == null) ? null : state.copy(codeMirrorParser);
        }

        @SuppressWarnings("unchecked")
        T result = (T)state;
        return result;
    }

    /**
     * Calculates mode at the beginning of line.
     *
     * @see #loadParserStateForBeginningOfLine
     */
    String getInitialMode(TaggableLine line) {
        State state = loadParserStateForBeginningOfLine(line);
        if (state == null) {
            return null;
        }
        return codeMirrorParser.getName(state);
    }

    private void saveEndOfLineParserState(Line line, State parserState) {
        State copiedParserState = parserState.copy(codeMirrorParser);
        line.putTag(LINE_TAG_END_OF_LINE_PARSER_STATE_SNAPSHOT, copiedParserState);
    }

    /**
     * Parse line text and return tokens.
     * <p/>
     * <p>New line char at the end of line is transformed to newline token.
     */
    private Array<Token> parseLine(State parserState, String lineText) throws ParserException {
        boolean endsWithNewline = lineText.endsWith("\n");
        lineText = endsWithNewline ? lineText.substring(0, lineText.length() - 1) : lineText;

        String tail = null;
        if (lineText.length() > LINE_LENGTH_LIMIT) {
            tail = lineText.substring(LINE_LENGTH_LIMIT);
            lineText = lineText.substring(0, LINE_LENGTH_LIMIT);
        }

        try {
            Stream stream = codeMirrorParser.createStream(lineText);
            Array<Token> tokens = Collections.createArray();
            while (!stream.isEnd()) {
                codeMirrorParser.parseNext(stream, parserState, tokens);
            }

            if (tail != null) {
                tokens.add(new Token(codeMirrorParser.getName(parserState), TokenType.ERROR, tail));
            }

            if (endsWithNewline) {
                tokens.add(Token.NEWLINE);
            }

            return tokens;
        } catch (Throwable t) {
            throw new ParserException(t);
        }
    }

    /**
     * Parse given line to the given column (optionally appending the given text)
     * and return result containing final parsing state and list of produced
     * tokens.
     *
     * @param appendedText
     *         {@link String} to be appended after a cursor position;
     *         if {@code null} then nothing is appended.
     * @return {@code null} if it is currently impossible to parse.
     */
    <T extends State> ParseResult<T> getParserState(Position position, String appendedText) {
        Line line = position.getLine();
        T parserState = loadParserStateForBeginningOfLine(line);
        if (parserState == null) {
            return null;
        }
        String lineText = line.getText().substring(0, position.getColumn());
        if (appendedText != null) {
            lineText = lineText + appendedText;
        }

        Array<Token> tokens;
        try {
            tokens = parseLine(parserState, lineText);
            return new ParseResult<T>(tokens, parserState);
        } catch (ParserException e) {
            Log.error(getClass(), "Could not parse line:", line, e);
            return null;
        }
    }
}
