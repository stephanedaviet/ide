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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class AbstractCreateFromTemplateForm<T extends Template> extends DialogWindow 
implements CreateFromTemplateDisplay<T>
{

   public static final int WIDTH = 550;

   public static final int HEIGHT = 350;
   
   private static final String ID = "ideCreateFileFromTemplateForm";
   
   private static final String ID_CREATE_BUTTON = "ideCreateFileFromTemplateFormCreateButton";
   
   private static final String ID_CANCEL_BUTTON = "ideCreateFileFromTemplateFormCancelButton";
   
   private static final String ID_DELETE_BUTTON = "ideCreateFileFromTemplateFormDeleteButton";
   
   private static final String ID_DYNAMIC_FORM = "ideCreateFileFromTemplateFormDynamicForm";
   
   private static final String FILE_NAME_FIELD = "ideCreateFileFromTemplateFormFileNameField";
   
   protected VLayout windowLayout;

   private IButton createButton;

   private IButton cancelButton;

   private IButton deleteButton;

   protected TemplateListGrid<T> templateListGrid;

   private TextField nameField;
   
   private AbstractCreateFromTemplatePresenter<T> presenter;
   
   public AbstractCreateFromTemplateForm(HandlerManager eventBus, AbstractCreateFromTemplatePresenter<T> presenter)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      initForm(eventBus);
      this.presenter = presenter;
   }
   
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }
   
   private void initForm(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      setTitle(getFormTitle());
      setCanDragResize(true);
      setShowMaximizeButton(true);

      windowLayout = new VLayout();
      windowLayout.setMargin(10);
      addItem(windowLayout);

      createTypeLayout();

      Layout l = new Layout();
      l.setHeight(10);
      windowLayout.addMember(l);

      windowLayout.addMember(getActionsForm());

      show();

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   abstract void createTypeLayout();

   private HLayout getActionsForm()
   {
      HLayout actionsLayout = new HLayout();
      actionsLayout.setHeight(35);
      actionsLayout.setWidth100();

      DynamicForm form = new DynamicForm();
      form.setID(ID_DYNAMIC_FORM);
      nameField = new TextField("Name", getNameFieldLabel());
      nameField.setName(FILE_NAME_FIELD);
      nameField.setWidth(200);
      nameField.setWrapTitle(false);
      form.setColWidths("*", "195");
      form.setItems(nameField);
      actionsLayout.addMember(form);

      Layout l = new Layout();
      l.setWidth100();
      actionsLayout.addMember(l);

      actionsLayout.addMember(getButtonsForm());
      return actionsLayout;
   }
   
   abstract String getCreateButtonTitle();
   
   abstract String getNameFieldLabel();
   
   abstract String getFormTitle();
   
   private DynamicForm getButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      createButton = new IButton(getCreateButtonTitle());
      createButton.setID(ID_CREATE_BUTTON);
      createButton.setWidth(75);
      createButton.setHeight(22);
      createButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(75);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      deleteButton = new IButton("Delete");
      deleteButton.setID(ID_DELETE_BUTTON);
      deleteButton.setWidth(75);
      deleteButton.setHeight(22);
      deleteButton.setIcon(Images.Buttons.DELETE);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(2);

      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(2);

      tbi.setButtons(deleteButton, delimiter1, createButton, delimiter2, cancelButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      return buttonsForm;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   public void closeForm()
   {
      destroy();
   }

   public void disableCreateButton()
   {
      createButton.disable();
   }

   public void enableCreateButton()
   {
      createButton.enable();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#selectLastTemplate()
    */
   public void selectLastTemplate()
   {
      templateListGrid.selectRecord(templateListGrid.getRecords().length - 1);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getTemplateListGrid()
    */
   public ListGridItem<T> getTemplateListGrid()
   {
      return templateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }
   
   public List<T> getTemplatesSelected()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableDeleteButton()
    */
   public void enableDeleteButton()
   {
      deleteButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableDeleteButton()
    */
   public void disableDeleteButton()
   {
      deleteButton.setDisabled(true);
   }
   

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableNameField()
    */
   public void enableNameField()
   {
      nameField.enable();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableNameField()
    */
   public void disableNameField()
   {
      nameField.disable();
   }

}
