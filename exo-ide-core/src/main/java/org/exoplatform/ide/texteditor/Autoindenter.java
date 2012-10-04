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

package org.exoplatform.ide.texteditor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.regexp.shared.RegExp;

import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.text.store.TextChange;
import org.exoplatform.ide.text.store.TextStoreMutator;
import org.exoplatform.ide.text.store.util.LineUtils;
import org.exoplatform.ide.texteditor.api.TextListener;
import org.exoplatform.ide.texteditor.documentparser.DocumentParser;
import org.exoplatform.ide.util.ListenerRegistrar.Remover;
import org.exoplatform.ide.util.StringUtils;
import org.exoplatform.ide.util.TextUtils;

/**
 * A class responsible for automatically adding indentation when appropriate.
 */
public class Autoindenter
{

   private static final RegExp WHITESPACES = RegExp.compile("^\\s*$");

   private interface IndentationStrategy
   {

      Runnable handleTextChange(TextChange textChange, TextStoreMutator editorDocumentMutator);
   }

   private static class PreviousLineMatchingIndentationStrategy implements IndentationStrategy
   {
      @Override
      public Runnable handleTextChange(TextChange textChange, final TextStoreMutator editorDocumentMutator)
      {
         String text = textChange.getText();
         if (!"\n".equals(text))
         {
            return null;
         }

         final int toInsert = TextUtils.countWhitespacesAtTheBeginningOfLine(textChange.getLine().getText());

         if (toInsert == 0)
         {
            return null;
         }

         final Line line = LineUtils.getLine(textChange.getLine(), 1);
         final int lineNumber = textChange.getLineNumber() + 1;
         return new Runnable()
         {
            @Override
            public void run()
            {
               String addend = StringUtils.getSpaces(toInsert);
               editorDocumentMutator.insertText(line, lineNumber, 0, addend);
            }
         };
      }
   }

   private static class CodeMirrorIndentationStrategy implements IndentationStrategy
   {
      private final DocumentParser documentParser;

      CodeMirrorIndentationStrategy(DocumentParser parser)
      {
         documentParser = parser;
      }

      @Override
      public Runnable handleTextChange(TextChange textChange, final TextStoreMutator editorDocumentMutator)
      {
         String text = textChange.getText();

         if (!"\n".equals(text))
         {
            // TODO: We should incrementally apply autoindention to
            //               multiline pastes.
            // TODO: Take electric characters into account:
            //               documentParser.getElectricCharacters.
            return null;
         }

         // TODO: Ask parser to reparse changed line.
         final Line line = LineUtils.getLine(textChange.getLine(), 1);

         // Special case: pressing ENTER in the middle of whitespaces line should
         // not fix indentation (use case: press ENTER on empty line).
         Line prevLine = textChange.getLine();
         if (WHITESPACES.test(prevLine.getText()) && WHITESPACES.test(line.getText()))
         {
            return null;
         }

         final int lineNumber = textChange.getLineNumber() + 1;
         final int indentation = documentParser.getIndentation(line);
         if (indentation < 0)
         {
            return null;
         }

         final int oldIndentation = TextUtils.countWhitespacesAtTheBeginningOfLine(line.getText());
         if (indentation == oldIndentation)
         {
            return null;
         }

         return new Runnable()
         {
            @Override
            public void run()
            {
               if (indentation < oldIndentation)
               {
                  editorDocumentMutator.deleteText(line, lineNumber, 0, oldIndentation - indentation);
               }
               else
               {
                  String addend = StringUtils.getSpaces(indentation - oldIndentation);
                  editorDocumentMutator.insertText(line, lineNumber, 0, addend);
               }
            }
         };
      }
   }

   /**
    * Creates an instance of {@link Autoindenter} that is configured to take on
    * the appropriate indentation strategy depending on the document parser.
    */
   public static Autoindenter create(DocumentParser documentParser, Editor editor)
   {
      if (documentParser.hasSmartIndent())
      {
         return new Autoindenter(new CodeMirrorIndentationStrategy(documentParser), editor);
      }
      return new Autoindenter(new PreviousLineMatchingIndentationStrategy(), editor);
   }

   private final Editor editor;

   private final IndentationStrategy indentationStrategy;

   private boolean isMutatingDocument;

   private final TextListener textListener = new TextListener()
   {
      @Override
      public void onTextChange(TextChange textChange)
      {
         handleTextChange(textChange);
      }
   };

   private final Remover textListenerRemover;

   private Autoindenter(IndentationStrategy indentationStrategy, Editor editor)
   {
      this.indentationStrategy = indentationStrategy;
      this.editor = editor;

      textListenerRemover = editor.getTextListenerRegistrar().add(textListener);
   }

   public void teardown()
   {
      textListenerRemover.remove();
   }

   private void handleTextChange(TextChange textChange)
   {
      if (isMutatingDocument
         || textChange.getType() != TextChange.Type.INSERT)
      {
         return;
      }

      final Runnable mutator = indentationStrategy.handleTextChange(textChange, editor.getEditorDocumentMutator());

      if (mutator == null)
      {
         return;
      }

      // We shouldn't be touching the document in this callback, so defer.
      Scheduler.get().scheduleFinally(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            isMutatingDocument = true;
            try
            {
               mutator.run();
            }
            finally
            {
               isMutatingDocument = false;
            }
         }
      });
   }
}
