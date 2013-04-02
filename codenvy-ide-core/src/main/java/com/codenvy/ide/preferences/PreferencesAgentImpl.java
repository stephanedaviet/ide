/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 */
package com.codenvy.ide.preferences;

import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Implements PreferencesAgent and returns all available preferences.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesAgentImpl implements PreferencesAgent {
    JsonArray<PreferencesPagePresenter> preferences;

    /** Create PreferencesAgent. */
    @Inject
    public PreferencesAgentImpl() {
        preferences = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void addPage(PreferencesPagePresenter page) {
        preferences.add(page);
    }

    /**
     * Returns all available preferences.
     *
     * @return
     */
    public JsonArray<PreferencesPagePresenter> getPreferences() {
        return preferences;
    }
}