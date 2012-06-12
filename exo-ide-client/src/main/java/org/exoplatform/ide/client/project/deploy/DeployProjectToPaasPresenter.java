/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.deploy;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.CreateProjectEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyHelper.KeyCode;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployProjectToPaasPresenter.java Dec 1, 2011 4:45:52 PM vereshchaka $
 * 
 */
public class DeployProjectToPaasPresenter implements DeployProjectToPaasHandler, ViewClosedHandler, VfsChangedHandler
{

   public interface Display extends IsView
   {

      /**
       * Get Cancel button.
       * 
       * @return
       */
      HasClickHandlers getCancelButton();

      /**
       * Get Back button.
       * 
       * @return
       */
      HasClickHandlers getBackButton();

      /**
       * Get Finish button.
       * 
       * @return
       */
      HasClickHandlers getFinishButton();

      /**
       * Get Select PaaS select item.
       * 
       * @return
       */
      HasValue<String> getSelectPaasField();

      /**
       * Set values for Select PaaS select item.
       * 
       * @param values
       */
      void setPaasValueMap(String[] values);

      /**
       * Set focus to Select PaaS select item.
       */
      void focusSelectPaasField();

      /**
       * Set widget according to selected PaaS.
       * 
       * @param widget
       */
      void setPaasWidget(Widget widget);

      /**
       * Hide PaaS widget
       */
      void hidePaas();

   }

   private Display display;

   private List<String> paases;

   private List<Paas> paasList;

   private VirtualFileSystemInfo vfsInfo;

   private String projectName;

   private String templateName;

   private String projectType;

   private ProjectModel createdProject;

   /**
    * Current paas;
    */
   private Paas paas;

   private PaasCallback paasCallback = new PaasCallback()
   {
      @Override
      public void onViewReceived(Composite composite)
      {
         if (composite != null)
         {
            display.setPaasWidget(composite);
         }
         else
         {
            paas = null;
            display.hidePaas();
            display.getSelectPaasField().setValue("None");
         }
      }

      @Override
      public void onValidate(boolean result)
      {
         if (result)
         {
            if (paas != null)
            {
               if (paas.canCreateProject())
               {
                  createEmptyProject();
               }
               else
               {
                  createProject();
               }

            }
         }

         // if form isn't valid, then do nothing
         // all validation messages must be shown by paases
      }

      @Override
      public void onProjectCreated(ProjectModel createdProject)
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
         IDE.fireEvent(new ProjectCreatedEvent(createdProject));
      }

      @Override
      public void projectCreationFailed()
      {
         Dialogs.getInstance().showError("Project could not be created");
      }

      @Override
      public void onDeploy(boolean result)
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }

   };

   public DeployProjectToPaasPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(DeployProjectToPaasEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getSelectPaasField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            paasSelected(event.getValue());
         }
      });

      if (display.getSelectPaasField() instanceof HasKeyPressHandlers)
      {
         ((HasKeyPressHandlers)display.getSelectPaasField()).addKeyPressHandler(new KeyPressHandler()
         {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
               if (KeyCode.ENTER == event.getNativeEvent().getKeyCode())
               {
                  beginCreateProject();
               }
            }
         });
      }

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            beginCreateProject();
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
            IDE.eventBus().fireEvent(new CreateProjectEvent(projectName, projectType));
         }
      });

      display.focusSelectPaasField();
   }

   private void beginCreateProject()
   {
      if (paas != null)
      {
         paas.validate();
      }
      else
      {
         createProject();
      }
   }

   private void paasSelected(String paasName)
   {
      if ("None".equals(paasName))
      {
         display.hidePaas();
         paas = null;
         return;
      }

      for (Paas p : paasList)
      {
         if (p.getName().equals(paasName))
         {
            paas = p;
            paas.getView(projectName, paasCallback);
         }
      }
   }

   private void createProject()
   {
      final IDELoader loader = new IDELoader();
      try
      {
         String parentId = vfsInfo.getRoot().getId();

         loader.show();
         TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), parentId, projectName, templateName,
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {
               @Override
               protected void onSuccess(final ProjectModel result)
               {
                  createdProject = result;
                  loader.hide();
                  if (paas != null)
                  {
                     // FIXME
                     // timer for allowing project to create fully
                     // find better solution!!!!!!!!!
                     new Timer()
                     {
                        @Override
                        public void run()
                        {
                           paas.deploy(result);
                        }
                     }.schedule(2000);
                  }

                  IDE.getInstance().closeView(display.asView().getId());
                  IDE.fireEvent(new ProjectCreatedEvent(result));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void createEmptyProject()
   {
      final IDELoader loader = new IDELoader();
      try
      {
         loader.show();
         final ProjectModel newProject = new ProjectModel();
         newProject.setName(projectName);
         newProject.setProjectType(projectType);

         VirtualFileSystem.getInstance().createProject(vfsInfo.getRoot(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(newProject))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  paas.createProject(newProject);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (Exception e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasHandler#onDeployProjectToPaas(org.exoplatform.ide.client.project.deploy.DeployProjectToPaasEvent)
    */
   @Override
   public void onDeployProjectToPaas(DeployProjectToPaasEvent event)
   {
      projectName = event.getProjectName();
      templateName = event.getTemplateName();
      projectType = event.getProjectType();

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());

      bindDisplay();

      paas = null;
      paases = new ArrayList<String>();
      paases.addAll(getPaasValues());

      display.setPaasValueMap(paases.toArray(new String[paases.size()]));
      display.getSelectPaasField().setValue(paases.get(0));

      if (paas != null)
      {
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               paas.getView(projectName, paasCallback);
            }
         });
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private List<String> getPaasValues()
   {
      List<String> paases = new ArrayList<String>();
      paases.add("None");

      this.paasList = IDE.getInstance().getPaases();
      for (Paas paas : this.paasList)
      {
         if (paas.getSupportedProjectTypes().contains(projectType))
         {
            if (paas.isFirstInDeployments())
            {
               paases.add(0, paas.getName());
               this.paas = paas;
            }
            else
            {
               paases.add(paas.getName());
            }
         }

      }
      return paases;
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

}
