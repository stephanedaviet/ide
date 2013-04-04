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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


/**
 * Recursively traverses the JSon Response to build tree Fodler model
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class FolderTreeUnmarshaller implements Unmarshallable<Folder> {

    private static final String CHILDREN = "children";

    private static final String TYPE = "itemType";

    private static final String ITEM = "item";

    private static final String ID = "id";

    private final Folder parentFolder;

    private final Project parentProject;

    /**
     * @param parentFolder
     * @param parentProject
     */
    public FolderTreeUnmarshaller(Folder parentFolder, Project parentProject) {
        this.parentFolder = parentFolder;
        this.parentProject = parentProject;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONObject object = JSONParser.parseLenient(response.getText()).isObject();
            getChildren(object.get(CHILDREN), parentFolder, parentProject);

        } catch (Exception exc) {
            String message = "Can't parse response " + response.getText();
            throw new UnmarshallerException(message, exc);
        }
    }

    /**
     * @param children
     * @param parentFolder
     * @param parentProject
     */
    private void getChildren(JSONValue children, Folder parentFolder, Project parentProject) {
        JSONArray itemsArray = children.isArray();

        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject itemObject = itemsArray.get(i).isObject();
            // Get item
            JSONObject item = itemObject.get(ITEM).isObject();

            String id = item.get(ID).isString().stringValue();

            String type = null;
            if (item.get(TYPE).isNull() == null) {
                type = item.get(TYPE).isString().stringValue();
            }

            // Project found in JSON Response
            if (Project.TYPE.equalsIgnoreCase(type)) {
                Log.error(this.getClass(), "Unsupported operation. Unmarshalling a child projects is not supported");
            }
            // Folder
            else if (Folder.TYPE.equalsIgnoreCase(type)) {
                Folder folder;

                // find if Folder Object already exists. This is a refresh usecase.
                Resource existingFolder = parentFolder.findChildById(id);
                // Make sure found resource is Folder
                if (existingFolder != null && Folder.TYPE.equalsIgnoreCase(existingFolder.getResourceType())) {
                    // use existing folder instance as
                    folder = (Folder)existingFolder;
                } else {
                    folder = new Folder(item);
                    parentFolder.addChild(folder);
                    folder.setProject(parentProject);
                }
                // recursively get project
                getChildren(itemObject.get(CHILDREN), folder, parentProject);
            }
            // File
            else if (File.TYPE.equalsIgnoreCase(type)) {
                File file = new File(item);
                parentFolder.addChild(file);
                file.setProject(parentProject);
            } else {
                Log.error(this.getClass(), "Unsupported Resource type: " + type);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Folder getPayload() {
        return parentFolder;
    }

}
