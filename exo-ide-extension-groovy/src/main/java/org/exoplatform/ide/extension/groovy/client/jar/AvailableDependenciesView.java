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
package org.exoplatform.ide.extension.groovy.client.jar;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.gwtframework.ui.client.util.ImageFactory;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.groovy.client.GroovyClientBundle;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.extension.groovy.shared.Attribute;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AvailableDependenciesView extends ViewImpl implements
org.exoplatform.ide.extension.groovy.client.jar.AvailableDependenciesPresenter.Display
{

   /**
    * ID of this view.
    */
   private static final String ID = "ideJARPackagesView";

   /**
    * Title of this view.
    */
   private static final String TITLE = "Available JAR Packages";

   /**
    * Initial width of this view.
    */
   private static int INITIAL_WIDTH = 650;

   /**
    * Initial height of this view.
    */
   private static int INITIAL_HEIGHT = 330;

   /**
    * UIBinder instance. 
    */
   private static AvailableDependenciesViewUiBinder uiBinder = GWT.create(AvailableDependenciesViewUiBinder.class);

   interface AvailableDependenciesViewUiBinder extends UiBinder<Widget, AvailableDependenciesView>
   {
   }
   
   /**
    * Tab panel to placing the JAR List Grid.
    */
   @UiField
   TabPanel jarsTabPanel;

   /**
    * Tab panel to placing the Properties List Grid.
    */
   @UiField
   TabPanel propertiesTabPanel;
   
   /**
    * Ok button.
    */
   @UiField
   ImageButton okButton;

   /**
    * List Grid for displaying the list of JAR files.
    */
   private JarFilesListGrid jarsListGrid;

   /**
    * List Grid for displaying attributes of JAR file.
    */
   private AttributesListGrid attributesListGrid;

   /**
    * Creates a new instance of this view.
    */
   public AvailableDependenciesView()
   {
      super(ID, "popup", TITLE, new Image(GroovyClientBundle.INSTANCE.jarLibrary()), INITIAL_WIDTH, INITIAL_HEIGHT);
      add(uiBinder.createAndBindUi(this));
      
      jarsListGrid = new JarFilesListGrid();
      jarsListGrid.setSize("100%", "100%");
      
      Image jarsIcon = new Image(GroovyClientBundle.INSTANCE.jarLibrary());
      
      Border b1 = new Border();
      b1.setSize("100%", "100%");
      b1.add(jarsListGrid);
      
      jarsTabPanel.addTab("jarList", jarsIcon, "JAR Libraries", b1, false);
      jarsTabPanel.selectTab(0);

      attributesListGrid = new AttributesListGrid();
      attributesListGrid.setSize("100%", "100%");
      Image propertiesIcon = ImageFactory.getImage("properties");
      
      Border b2 = new Border();
      b2.setSize("100%", "100%");
      b2.add(attributesListGrid);
      
      propertiesTabPanel.addTab("properties", propertiesIcon, "Properties", b2, false);
      propertiesTabPanel.selectTab(0);
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.AvailableDependenciesPresenter.JarDiscoveryPresenter.Display#getJarsListGrid()
    */
   @Override
   public ListGridItem<Jar> getJarsListGrid()
   {
      return jarsListGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.AvailableDependenciesPresenter.JarDiscoveryPresenter.Display#getPropertiesGrid()
    */
   @Override
   public ListGridItem<Attribute> getAttributesGrid()
   {
      return attributesListGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.AvailableDependenciesPresenter.JarDiscoveryPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

}
