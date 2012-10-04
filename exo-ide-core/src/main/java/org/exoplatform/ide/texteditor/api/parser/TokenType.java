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

package org.exoplatform.ide.texteditor.api.parser;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.runtime.Assert;

/**
 * Enumeration of javascript and python style tokens returned from
 * {@link Token#getStyle()}.
 */
public enum TokenType {
   ATOM("atom"), //
   ATTRIBUTE("attribute"),// 
   BUILTIN("builtin"), //
   COMMENT("comment"), //
   DEF("def"), //
   ERROR("error"), //
   KEYWORD("keyword"), // 
   META("meta"), // 
   NUMBER("number"), // 
   OPERATOR("operator"), // 
   PROPERTY("property"), //
   STRING("string"), //

   /**
    * RegExp in JS.
    *
    * <p>NB: Currently not generated by other parsers we use.
    */
   REGEXP("string-2"), TAG("tag"), VARIABLE("variable"), VARIABLE2("variable-2"), WORD("word"),

   // Artificial values
   NEWLINE("newline"),

   // "nil" is a workaround - getting null from map works like getting "null".
   NULL("nil"), WHITESPACE("whitespace");

   private final String typeName;

   TokenType(String typeName)
   {
      this.typeName = typeName;
   }

   public String getTypeName()
   {
      return typeName;
   }

   private static JsonStringMap<TokenType> typesMap;

   private static JsonStringMap<TokenType> getTypesMap()
   {
      if (typesMap == null)
      {
         JsonStringMap<TokenType> temp = JsonCollections.createStringMap();
         TokenType[] types = TokenType.values();
         for (int i = 0, l = types.length; i < l; i++)
         {
            TokenType type = types[i];
            temp.put(type.getTypeName(), type);
         }
         typesMap = temp;
      }
      return typesMap;
   }

   public static TokenType resolveTokenType(String cmTokenType, String tokenValue)
   {
      TokenType type = getTypesMap().get(cmTokenType);
      Assert.isLegal(cmTokenType == null || type != null, cmTokenType);
      if (type == null)
      {
         if (("\n").equals(tokenValue))
         {
            type = TokenType.NEWLINE;
         }
         else if (tokenValue.trim().length() == 0)
         {
            type = TokenType.WHITESPACE;
         }
         else
         {
            type = TokenType.NULL;
         }
      }
      return type;
   }
}
