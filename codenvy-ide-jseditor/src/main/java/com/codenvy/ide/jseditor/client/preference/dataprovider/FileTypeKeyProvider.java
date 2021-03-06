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
package com.codenvy.ide.jseditor.client.preference.dataprovider;

import com.codenvy.ide.api.filetypes.FileType;
import com.google.gwt.view.client.ProvidesKey;

/**
 * {@link ProvidesKey} for FileType.
 *
 * @author "Mickaël Leduque"
 */
public class FileTypeKeyProvider implements ProvidesKey<FileType> {
    @Override
    public Object getKey(final FileType item) {
        return item.getId();
    }
}
