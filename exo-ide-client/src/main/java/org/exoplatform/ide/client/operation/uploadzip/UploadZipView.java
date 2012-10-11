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
package org.exoplatform.ide.client.operation.uploadzip;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadZipView extends ViewImpl implements
   org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display
{

   private static final String ID = "ideUploadForm";

   private static final String OVERWRITE_HIDDED_FIELD = "overwrite";

   /**
    * Initial width of this view.
    */
   private static final int WIDTH = 450;

   /**
    * Initial height of this view.
    */
   private static final int HEIGHT = 165;

   private static final String UPLOAD_FOLDER_TITLE = IDE.UPLOAD_CONSTANT.uploadFolderTitle();

   private static UploadZipViewUiBinder uiBinder = GWT.create(UploadZipViewUiBinder.class);

   interface UploadZipViewUiBinder extends UiBinder<Widget, UploadZipView>
   {
   }

   @UiField
   FormPanel uploadForm;

   @UiField
   TextInput fileNameField;

   @UiField
   HorizontalPanel postFieldsPanel;

   @UiField
   FileUploadInput fileUploadInput;

   @UiField
   ImageButton uploadButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   CheckBox overwriteField;

   private Hidden overwriteHiddenField;

   public UploadZipView()
   {
      super(ID, "modal", UPLOAD_FOLDER_TITLE, new Image(IDEImageBundle.INSTANCE.upload()), WIDTH, HEIGHT, false);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
      overwriteHiddenField = new Hidden(OVERWRITE_HIDDED_FIELD);
   }

   @Override
   public HasClickHandlers getUploadButton()
   {
      return uploadButton;
   }

   @Override
   public void setUploadButtonEnabled(boolean enabled)
   {
      uploadButton.setEnabled(enabled);
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public FormPanel getUploadForm()
   {
      return uploadForm;
   }

   @Override
   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   @Override
   public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType)
   {
      Hidden locationField = new Hidden(FormFields.LOCATION, location);
      postFieldsPanel.add(locationField);
   }

   @Override
   public HasFileSelectedHandler getFileUploadInput()
   {
      return fileUploadInput;
   }

   /**
    * @see org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display#setOverwriteHiddedField(java.lang.Boolean)
    */
   @Override
   public void setOverwriteHiddedField(Boolean overwrite)
   {
      overwriteHiddenField.setValue(String.valueOf(overwrite));
      if (postFieldsPanel.getWidgetIndex(overwriteHiddenField) == -1)
         postFieldsPanel.add(overwriteHiddenField);
   }

   /**
    * @see org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display#getOverwriteAllField()
    */
   @Override
   public HasValue<Boolean> getOverwriteAllField()
   {
      return overwriteField;
   }

}