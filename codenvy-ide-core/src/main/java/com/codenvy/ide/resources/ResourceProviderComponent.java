/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.resources;

import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.collections.IntegerMap.IterationCallback;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.marshal.JSONSerializer;
import com.codenvy.ide.resources.marshal.ProjectModelProviderAdapter;
import com.codenvy.ide.resources.marshal.ProjectModelUnmarshaller;
import com.codenvy.ide.resources.marshal.VFSInfoUnmarshaller;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Link;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Implementation of Resource Provider
 *
 * @author Nikolay Zamosenchuk
 */
@Singleton
public class ResourceProviderComponent implements ResourceProvider, Component {
    /** Used for compatibility with IDE-VFS 1.x */
    private static final String DEPRECATED_PROJECT_TYPE = "deprecated.project.type";
    protected final ModelProvider            genericModelProvider;
    /** Fully qualified URL to root folder of VFS */
    private final   String                   workspaceURL;
    private final   StringMap<ModelProvider> modelProviders;
    private final   IntegerMap<FileType>     fileTypes;
    private final   EventBus                 eventBus;
    private final   FileType                 defaultFile;
    protected       VirtualFileSystemInfo    vfsInfo;
    private         Loader                   loader;
    @SuppressWarnings("unused")
    private boolean initialized = false;
    private Project    activeProject;
    private DtoFactory dtoFactory;

    /**
     * Resources API for client application.
     * It deals with VFS to retrieve the content of  the files
     */
    @Inject
    public ResourceProviderComponent(ModelProvider genericModelProvider,
                                     Loader loader,
                                     EventBus eventBus,
                                     @Named("defaultFileType") FileType defaultFile,
                                     @Named("restContext") String restContext,
                                     DtoFactory dtoFactory) {
        super();
        this.genericModelProvider = genericModelProvider;
        this.eventBus = eventBus;
        this.defaultFile = defaultFile;
        this.dtoFactory = dtoFactory;
        this.workspaceURL = restContext + "/vfs/" + Utils.getWorkspaceName() + "/v2";
        this.modelProviders = Collections.<ModelProvider>createStringMap();
        this.fileTypes = Collections.createIntegerMap();
        this.loader = loader;
    }

    @Override
    public void start(final Callback<Component, ComponentException> callback) {
        AsyncRequestCallback<VirtualFileSystemInfo> internalCallback =
                new AsyncRequestCallback<VirtualFileSystemInfo>(new VFSInfoUnmarshaller()) {
                    @Override
                    protected void onSuccess(VirtualFileSystemInfo result) {
                        vfsInfo = result;
                        initialized = true;
                        // notify Component started
                        callback.onSuccess(ResourceProviderComponent.this);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        // notify Component failed
                        callback.onFailure(new ComponentException(
                                "Failed to start Resource Manager. Cause:" + exception.getMessage(),
                                ResourceProviderComponent.this));
                        Log.error(ResourceProviderComponent.class, exception);
                    }
                };

        this.vfsInfo = internalCallback.getPayload();
        try {
            AsyncRequest.build(RequestBuilder.GET, workspaceURL).send(internalCallback);
        } catch (RequestException exception) {
            // notify Component failed
            callback.onFailure(new ComponentException("Failed to start Resource Manager. Cause:" + exception.getMessage(),
                                                      ResourceProviderComponent.this));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void getProject(final String name, final AsyncCallback<Project> callback) {
        // create internal wrapping Request Callback with proper Unmarshaller
        AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
                new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(this)) {
                    @Override
                    protected void onSuccess(ProjectModelProviderAdapter result) {
                        Folder rootFolder = vfsInfo.getRoot();

                        Project project = result.getProject();
                        project.setParent(rootFolder);
                        project.setProject(project);
                        project.setVFSInfo(vfsInfo);

                        rootFolder.getChildren().clear();
                        rootFolder.addChild(project);
                        if (activeProject != null) {
                            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
                        }

                        activeProject = project;

                        // get project structure
                        project.refreshTree(new AsyncCallback<Project>() {
                            @Override
                            public void onSuccess(Project project) {
                                eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                                callback.onSuccess(project);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });

                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        try {
            // get Project Item by path
            String url = vfsInfo.getUrlTemplates().get((Link.REL_ITEM_BY_PATH)).getHref() + "?itemType=" + Project.TYPE;
            url = URL.decode(url).replace("[path]", name);
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    public void getFolder(final Folder folder, final AsyncCallback<Folder> callback) {
        activeProject.refreshTree(folder, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
                Resource f = result.findChildById(folder.getId());
                if (f != null && !f.getId().equals(result.getId())) {
                    eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(f));
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void listProjects(final AsyncCallback<String> callback) {
        // internal callback
        AsyncRequestCallback<String> internalCallback =
                new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        try {
            String param = "propertyFilter=*&itemType=" + Project.TYPE;
            AsyncRequest
                    .build(RequestBuilder.GET, vfsInfo.getRoot().getLinkByRelation(Link.REL_CHILDREN).getHref() + "?" + param)
                    .loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void createProject(String name, Array<Property> properties, final AsyncCallback<Project> callback) {
        final Folder rootFolder = vfsInfo.getRoot();
        // create internal wrapping Request Callback with proper Unmarshaller
        AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
                new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(this)) {
                    @Override
                    protected void onSuccess(ProjectModelProviderAdapter result) {
                        Project project = result.getProject();
                        project.setParent(rootFolder);
                        rootFolder.getChildren().clear();
                        rootFolder.addChild(project);
                        project.setProject(project);
                        project.setVFSInfo(vfsInfo);
                        if (activeProject != null) {
                            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
                        }
                        activeProject = project;

                        // get project structure
                        project.refreshTree(new AsyncCallback<Project>() {
                            @Override
                            public void onSuccess(Project project) {
                                eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                                callback.onSuccess(project);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });

                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        // perform request
        String url = rootFolder.getLinkByRelation(Link.REL_CREATE_PROJECT).getHref();
        url = URL.decode(url).replace("[name]", name);
        // DEPRECATED type not used anymore in 2.0
        url = url.replace("[type]", DEPRECATED_PROJECT_TYPE);
        url = URL.encode(url);
        loader.setMessage("Creating new project...");
        try {
            AsyncRequest.build(RequestBuilder.POST, url)
                        .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(properties).toString())
                        .header(HTTPHeader.CONTENT_TYPE, "application/json").loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void registerModelProvider(String language, ModelProvider modelProvider) {
        modelProviders.put(language, modelProvider);
    }

    /** {@inheritDoc} */
    @Override
    public ModelProvider getModelProvider(String language) {
        if (language != null) {
            ModelProvider modelProvider = modelProviders.get(language);
            if (modelProvider != null) {
                return modelProvider;
            }
        }
        // return generic model provider
        return genericModelProvider;
    }

    /** {@inheritDoc} */
    @Override
    public Project getActiveProject() {
        return activeProject;
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveProject(Project project) {
        this.activeProject = project;
    }

    /** {@inheritDoc} */
    @Override
    public void registerFileType(FileType fileType) {
        fileTypes.put(fileType.getId(), fileType);
    }

    /** {@inheritDoc} */
    @Override
    public FileType getFileType(File file) {
        String mimeType = file.getMimeType();
        final String name = file.getName();
        final Array<FileType> filtered = Collections.createArray();
        final Array<FileType> nameMatch = Collections.createArray();
        fileTypes.iterate(new IterationCallback<FileType>() {

            @Override
            public void onIteration(int key, FileType val) {
                if (val.getNamePattern() != null) {
                    RegExp regExp = RegExp.compile(val.getNamePattern());
                    if (regExp.test(name)) {
                        nameMatch.add(val);
                    }
                } else {
                    filtered.add(val);
                }
            }
        });
        if (!nameMatch.isEmpty()) {
            //TODO what if name matches more than one
            return nameMatch.get(0);
        }
        for (FileType type : filtered.asIterable()) {
            if (type.getMimeTypes().contains(mimeType)) {
                return type;
            }
        }
        String extension = getFileExtension(name);
        if (extension != null) {
            for (FileType type : filtered.asIterable()) {
                if (extension.equals(type.getExtension())) {
                    return type;
                }
            }
        }
        return defaultFile;

    }

    /**
     * @param name
     * @return
     */
    private String getFileExtension(String name) {
        int lastDotPos = name.lastIndexOf('.');
        //file has no extension
        if (lastDotPos < 0) {
            return null;
        }
        return name.substring(lastDotPos + 1);
    }

    /** {@inheritDoc} */
    @Override
    public VirtualFileSystemInfo getVfsInfo() {
        return vfsInfo;
    }

    /** {@inheritDoc} */
    @Override
    public String getRootId() {
        return vfsInfo.getRoot().getId();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Resource item, final AsyncCallback<String> callback) {
        final Folder parent = item.getParent();
        if (activeProject == null) {
            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // remove from the list of child
                    parent.removeChild(item);
                    eventBus.fireEvent(ResourceChangedEvent.createResourceDeletedEvent(item));
                    callback.onSuccess(item.getName());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };
            // TODO check with lock
            String url = item.getLinkByRelation(Link.REL_DELETE).getHref();

            if (File.TYPE.equals(item.getResourceType()) && ((File)item).isLocked()) {
                url = URL.decode(url).replace("[lockToken]", ((File)item).getLock().getLockToken());
            }
            loader.setMessage("Deleting item...");
            try {
                AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
            } catch (RequestException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            activeProject.deleteChild(item, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(Void result) {
                    if (item instanceof Project && !(parent instanceof Project)) {
                        showListProjects();
                        //TODO onSuccess
                        callback.onSuccess(item.toString());
                    } else if (parent instanceof Project) {
                        getProject(parent.getName(), new AsyncCallback<Project>() {
                            @Override
                            public void onSuccess(Project result) {
                                callback.onSuccess(result.toString());
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }
                        });
                    } else {
                        getFolder(parent, new AsyncCallback<Folder>() {
                            @Override
                            public void onSuccess(Folder result) {
                                if (item instanceof File) {
                                    eventBus.fireEvent(new FileEvent((File)item, FileEvent.FileOperation.CLOSE));
                                }
                                callback.onSuccess(result.toString());
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });
                    }
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showListProjects() {
        if (activeProject != null) {
            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
        }
        activeProject = null;

        final Folder rootFolder = vfsInfo.getRoot();
        rootFolder.getChildren().clear();

        listProjects(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Array<Resource> projects = Collections.createArray();
                rootFolder.setChildren(projects);
                eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(null));
                ItemList itemList = dtoFactory.createDtoFromJson(result, ItemList.class);
                for (Item item : itemList.getItems()) {
                    Project project = new Project(eventBus);
                    project.init(JSONParser.parseStrict(dtoFactory.toJson(item)).isObject());
                    rootFolder.addChild(project);
                    eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceProviderComponent.class, "Can not get list of projects", caught);
            }
        });
    }
}