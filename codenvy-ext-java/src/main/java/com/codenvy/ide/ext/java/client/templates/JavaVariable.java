/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.templates;

import com.codenvy.ide.ext.java.client.templates.CompilationUnitCompletion.Variable;
import com.codenvy.ide.ext.java.client.templates.api.TemplateVariableType;

public final class JavaVariable extends MultiVariable {
    private String fParamType;

    public JavaVariable(TemplateVariableType type, String name, int[] offsets) {
        super(type, name, offsets);
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.template.contentassist.MultiVariable#toString(java.lang.Object)
     * @since 3.3
     */
    @Override
    public String toString(Object object) {
        if (object instanceof Variable)
            return ((Variable)object).getName();
        return super.toString(object);
    }

    /**
     * Returns the type given as parameter to this variable.
     *
     * @return the type given as parameter to this variable
     */
    public String getParamType() {
        return fParamType;
    }

    /**
     * @param paramType
     *         the paramType
     * @since 3.3
     */
    public void setParamType(String paramType) {
        fParamType = paramType;
    }
}