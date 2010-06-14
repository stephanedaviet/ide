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
package org.exoplatform.ideall.client.autocompletion.js;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.TokenTemplate;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.autocompletion.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JavaScriptTokenCollector implements TokenCollector
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TokensCollectedCallback tokensCollectedCallback;
   
   private static List<Token> keywords = new ArrayList<Token>();
   
   private static List<Token> templates = new ArrayList<Token>();
   
   static
   {
      keywords.add(new Token("break", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("case", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("catch", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("const", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("continue", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("default", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("delete", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("do", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("else", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("export", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("false", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("while", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("for", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("function", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("if", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("import", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("in", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("instanceOf", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("label", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("let", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("new", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("null", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("return", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("switch", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("this", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("throw", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("true", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("try", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("typeof", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("var", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("void", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("while", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("with", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("yield", TokenType.KEYWORD, 0, null));
      keywords.add(new Token("yield", TokenType.KEYWORD, 0, null));
      
      templates.add(new TokenTemplate("for","for-iterate over array", "for (var i = 0; i < array.length; i++)\n{\n\n}"));
      templates.add(new TokenTemplate("if", "if-condition", "if (condition)\n{\n\n}"));
      templates.add(new TokenTemplate("if", "if-condition-else", "if (condition)\n{\n\n}\nelse\n{\n\n}"));
      templates.add(new TokenTemplate("try", "try-catch", "try\n{\n\n}\ncatch(e)\n{\n\n}"));
      
   }

   public JavaScriptTokenCollector(HandlerManager eventBus, ApplicationContext context,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.tokensCollectedCallback = tokensCollectedCallback;
      
   }

   public void getTokens(String prefix, List<Token> tokenFromParser)
   {
      
      List<Token> tokens = new ArrayList<Token>();
      tokens.addAll(keywords);
      tokens.addAll(templates);
      
      
//      tokens.add(new Token("b", TokenType.VARIABLE, 0));
//      tokens.add(new Token("bb", TokenType.VARIABLE, 0));
//      tokens.add(new Token("bbb", TokenType.VARIABLE, 0));
//      
//      tokens.add(new Token("c", TokenType.FUNCTION, 0));
//      tokens.add(new Token("ca", TokenType.FUNCTION, 0));
//      tokens.add(new Token("cat", TokenType.FUNCTION, 0));
//     
      tokens.addAll(tokenFromParser);
      tokensCollectedCallback.onTokensCollected(tokens);
   }


}
