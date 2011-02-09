/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterAutocompleteEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.ConfigureBuildPathPresenter;
import org.exoplatform.ide.extension.groovy.client.codeassistant.AssistImportDeclarationManager;
import org.exoplatform.ide.extension.groovy.client.codeassistant.autocompletion.GroovyTokenCollector;
import org.exoplatform.ide.extension.groovy.client.codeassistant.autocompletion.GroovyTokenWidgetFactory;
import org.exoplatform.ide.extension.groovy.client.controls.ConfigureBuildPathCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.PreviewWadlOutputCommand;
import org.exoplatform.ide.extension.groovy.client.controls.RunGroovyServiceCommand;
import org.exoplatform.ide.extension.groovy.client.controls.SetAutoloadCommand;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.ValidateGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.event.PreviewWadlOutputEvent;
import org.exoplatform.ide.extension.groovy.client.event.PreviewWadlOutputHandler;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadEvent;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.DeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.RunGroovyServiceCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.UndeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.ValidateGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.service.codeassistant.CodeAssistantServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.ui.GroovyServiceOutputPreviewForm;
import org.exoplatform.ide.extension.groovy.client.util.GroovyPropertyUtil;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyModule extends Extension implements RestServiceOutputReceivedHandler, SetAutoloadHandler,
   PreviewWadlOutputHandler, WadlServiceOutputReceiveHandler, InitializeServicesHandler,
   ApplicationSettingsReceivedHandler, EditorActiveFileChangedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private IDEConfiguration configuration;

   private Map<String, String> lockTokens;

   //need for http://jira.exoplatform.org/browse/IDE-347
   //undeploy service on cancel 
   private boolean undeployOnCancel = false;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(InitializeServicesEvent.TYPE, this);

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New REST Service", "REST Service",
         "Create REST Service", Images.FileType.REST_SERVICE, MimeType.GROOVY_SERVICE)));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New POGO", "POGO", "Create POGO",
         Images.FileType.GROOVY, MimeType.APPLICATION_GROOVY)));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Template", "Template",
         "Create Template", Images.FileType.GROOVY_TEMPLATE, MimeType.GROOVY_TEMPLATE)));

      eventBus.fireEvent(new RegisterControlEvent(new SetAutoloadCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ConfigureBuildPathCommand(), DockTarget.NONE));
      eventBus.fireEvent(new RegisterControlEvent(new ValidateGroovyCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new DeployGroovyCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new UndeployGroovyCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new RunGroovyServiceCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new DeployGroovySandboxCommand(eventBus), DockTarget.TOOLBAR, true));
      eventBus
         .fireEvent(new RegisterControlEvent(new UndeployGroovySandboxCommand(eventBus), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new PreviewWadlOutputCommand(), DockTarget.TOOLBAR, true));

      handlers.addHandler(RestServiceOutputReceivedEvent.TYPE, this);
      handlers.addHandler(SetAutoloadEvent.TYPE, this);
      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      //      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      new RunGroovyServiceCommandHandler(eventBus);
      new ValidateGroovyCommandHandler(eventBus);
      new DeployGroovyCommandHandler(eventBus);
      new UndeployGroovyCommandHandler(eventBus);
      new AssistImportDeclarationManager(eventBus);
      new ConfigureBuildPathPresenter(eventBus);

      GroovyPluginImageBundle.INSTANCE.css().ensureInjected();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      configuration = event.getApplicationConfiguration();
      new GroovyServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      new WadlServiceImpl(eventBus, event.getLoader());
      new CodeAssistantServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      GroovyTokenWidgetFactory groovyTokenWidgetFactory =
         new GroovyTokenWidgetFactory(event.getApplicationConfiguration().getContext());
      GroovyTokenCollector groovyTokenCollector = new GroovyTokenCollector(eventBus);
      eventBus.fireEvent(new RegisterAutocompleteEvent(MimeType.GROOVY_SERVICE, groovyTokenWidgetFactory,
         groovyTokenCollector));
      eventBus.fireEvent(new RegisterAutocompleteEvent(MimeType.APPLICATION_GROOVY, groovyTokenWidgetFactory,
         groovyTokenCollector));
   }

   /**
    * @see org.exoplatform.ide.groovy.event.RestServiceOutputReceivedHandler#onRestServiceOutputReceived(org.exoplatform.ide.groovy.event.RestServiceOutputReceivedEvent)
    */
   public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
   {
      if (event.getException() == null)
      {
         String response = event.getOutput().getResponseAsHtmlString();

         OutputEvent outputEvent = new OutputEvent(response, OutputMessage.Type.OUTPUT);
         eventBus.fireEvent(outputEvent);
      }
      else
      {
         ServerException exception = (ServerException)event.getException();

         if (exception.isErrorMessageProvided())
         {
            String message =
               "<b>" + event.getOutput().getUrl() + "</b>&nbsp;" + exception.getHTTPStatus() + "&nbsp;"
                  + exception.getStatusText() + "<hr>" + exception.getMessage();

            OutputEvent errorEvent = new OutputEvent(message, OutputMessage.Type.ERROR);
            eventBus.fireEvent(errorEvent);
         }
         else
         {
            String message =
               "<b>" + event.getOutput().getUrl() + "</b>&nbsp;" + exception.getHTTPStatus() + "&nbsp;"
                  + exception.getStatusText();
            OutputEvent errorEvent = new OutputEvent(message, OutputMessage.Type.ERROR);
            eventBus.fireEvent(errorEvent);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void onSetAutoload(SetAutoloadEvent event)
   {
      Property jcrContentProperty =
         GroovyPropertyUtil.getProperty(activeFile.getProperties(), ItemProperty.JCR_CONTENT);
      Property autoloadProperty =
         GroovyPropertyUtil.getProperty(jcrContentProperty.getChildProperties(), ItemProperty.EXO_AUTOLOAD);
      autoloadProperty.setValue("" + event.isAutoload());

      VirtualFileSystem.getInstance().saveProperties(activeFile, lockTokens.get(activeFile.getHref()));
   }

   /**
    * {@inheritDoc}
    */
   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
      undeployOnCancel = event.isUndeployOnCansel();
      String content = activeFile.getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String path = content.substring(indStart + 1, indEnd);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }

      String url = configuration.getContext() + path;
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);
      WadlService.getInstance().getWadl(url);
   }

   /**
    * {@inheritDoc}
    */
   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      handlers.removeHandler(WadlServiceOutputReceivedEvent.TYPE);
      new GroovyServiceOutputPreviewForm(eventBus, event.getApplication(), undeployOnCancel);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
