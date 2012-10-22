/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.java.client.templates;

import org.exoplatform.ide.java.client.JavaExtension;
import org.exoplatform.ide.java.client.templates.api.Template;
import org.exoplatform.ide.java.client.templates.api.TemplateBuffer;
import org.exoplatform.ide.java.client.templates.api.TemplateContext;
import org.exoplatform.ide.java.client.templates.api.TemplateException;
import org.exoplatform.ide.java.client.templates.api.TemplateTranslator;
import org.exoplatform.ide.java.client.templates.api.TemplateVariableResolver;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.DefaultLineTracker;
import org.exoplatform.ide.text.LineTracker;
import org.exoplatform.ide.text.Region;

import java.util.Iterator;

public class CodeTemplateContext extends TemplateContext
{

   private String fLineDelimiter;

   public CodeTemplateContext(String contextTypeName, String lineDelim)
   {
      super(JavaExtension.get().getTemplateContextRegistry().getContextType(contextTypeName));
      fLineDelimiter = lineDelim;
   }

   /*
    * @see org.eclipse.jdt.internal.corext.template.TemplateContext#evaluate(org.eclipse.jdt.internal.corext.template.Template)
    */
   @Override
   public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException
   {
      // test that all variables are defined
      Iterator<TemplateVariableResolver> iterator = getContextType().resolvers();
      while (iterator.hasNext())
      {
         TemplateVariableResolver var = iterator.next();
         if (var instanceof CodeTemplateContextType.CodeTemplateVariableResolver)
         {
            Assert.isNotNull(getVariable(var.getType()), "Variable " + var.getType() + "not defined"); //$NON-NLS-1$ //$NON-NLS-2$
         }
      }

      if (!canEvaluate(template))
         return null;

      String pattern = changeLineDelimiter(template.getPattern(), fLineDelimiter);

      TemplateTranslator translator = new TemplateTranslator();
      TemplateBuffer buffer = translator.translate(pattern);
      getContextType().resolve(buffer, this);
      return buffer;
   }

   private static String changeLineDelimiter(String code, String lineDelim)
   {
      try
      {
         LineTracker tracker = new DefaultLineTracker();
         tracker.set(code);
         int nLines = tracker.getNumberOfLines();
         if (nLines == 1)
         {
            return code;
         }

         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < nLines; i++)
         {
            if (i != 0)
            {
               buf.append(lineDelim);
            }
            Region region = tracker.getLineInformation(i);
            String line = code.substring(region.getOffset(), region.getOffset() + region.getLength());
            buf.append(line);
         }
         return buf.toString();
      }
      catch (BadLocationException e)
      {
         // can not happen
         return code;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.corext.template.TemplateContext#canEvaluate(org.eclipse.jdt.internal.corext.template.Template)
    */
   @Override
   public boolean canEvaluate(Template template)
   {
      return true;
   }

   public void setCompilationUnitVariables(File file)
   {

      setVariable(CodeTemplateContextType.FILENAME, file.getName());
      setVariable(CodeTemplateContextType.PACKAGENAME, getPackage(file));
      setVariable(CodeTemplateContextType.PROJECTNAME, file.getProject().getName());
   }

   private String getPackage(File file)
   {
      Project project = file.getProject();
      //TODO magic constants
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder")
            : "src/main/java";
      String parentPath = file.getPath();
      String packageText = parentPath.substring((project.getPath() + "/" + sourcePath + "/").length());
      if (packageText.isEmpty())
         return "";
      if (packageText.endsWith("/"))
         packageText = packageText.substring(0, packageText.length() - 1);
      return packageText.replaceAll("/", ".");
   }
}
