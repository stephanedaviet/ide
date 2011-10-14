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
package org.exoplatform.ide.client.project;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.project.CreateProjectPresenter.Display;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateProjectForm extends ViewImpl implements Display
{
   private static final int WIDTH = 370;

   private static final int HEIGHT = 210;
   
   private static final String ID = CreateProjectForm.class.getName();
   
   @UiField
   TextField projectName;
   
   @UiField
   TextField projectType;
   
   @UiField
   ImageButton createButton;
   
   @UiField
   ImageButton cancelButton; 
   
   
   private static CreateProjectFormUiBinder uiBinder = GWT.create(CreateProjectFormUiBinder.class);

   interface CreateProjectFormUiBinder extends UiBinder<Widget, CreateProjectForm>
   {
   }
   
   public CreateProjectForm()
   {
      super(ID, ViewType.MODAL, "Create Project", null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }
   
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setProjectType(List<String> types)
   {
      projectType.setValue(types.get(0));
   }

   @Override
   public void setProjectName(String name)
   {
      projectName.setValue(name);
   }

   @Override
   public HasValue<String> getProjectName()
   {
      return projectName;
   }

   @Override
   public HasValue<String> getProjectType()
   {
      return projectType;
   }

}
