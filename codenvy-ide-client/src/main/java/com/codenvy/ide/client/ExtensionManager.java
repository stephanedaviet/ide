package com.codenvy.ide.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.json.JsonCollections;
/**
 * THIS CLASS WILL BE OVERRIDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT.
 */
@Singleton
@SuppressWarnings("rawtypes")
public class ExtensionManager
{

   /** Contains the map will all the Extnesion Providers <FullClassFQN, Provider>. */
   protected final JsonStringMap<Provider> extensions = JsonCollections.createStringMap();

   /** Constructor that accepts all the Extension found in IDE package */
   @Inject
   public ExtensionManager(
      Provider<com.codenvy.ide.extension.demo.DemoExtension> demoextension,
      Provider<com.codenvy.ide.extension.maven.client.BuilderExtension> builderextension,
      Provider<com.codenvy.ide.extension.tasks.TasksExtension> tasksextension,
      Provider<com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension> cloudfoundryextension,
      Provider<com.codenvy.ide.java.client.JavaExtension> javaextension,
      Provider<com.codenvy.ide.extension.css.CssExtension> cssextension
   )
   {
      this.extensions.put("com.codenvy.ide.extension.demo.DemoExtension",demoextension);
      this.extensions.put("com.codenvy.ide.extension.maven.client.BuilderExtension",builderextension);
      this.extensions.put("com.codenvy.ide.extension.tasks.TasksExtension",tasksextension);
      this.extensions.put("com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension",cloudfoundryextension);
      this.extensions.put("com.codenvy.ide.java.client.JavaExtension",javaextension);
      this.extensions.put("com.codenvy.ide.extension.css.CssExtension",cssextension);
   }

   /** Returns  the map will all the Extnesion Providers <FullClassFQN, Provider>. */
   public JsonStringMap<Provider> getExtensions()
   {
      return extensions;
   }
}