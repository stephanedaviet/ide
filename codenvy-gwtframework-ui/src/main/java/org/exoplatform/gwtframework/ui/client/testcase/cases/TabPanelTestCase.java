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
package org.exoplatform.gwtframework.ui.client.testcase.cases;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.tab.TabButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabPanelTestCase extends TestCase
{

   private Toolbar buttonsPanel;

   private TabPanel tabPanel;

   @Override
   public void draw()
   {
      DOM.setStyleAttribute(getElement(), "overflow", "hidden");

      hideControlsPanel();

      Grid grid = new Grid(2, 1);
      grid.setSize("100%", "100%");
      DOM.setStyleAttribute(grid.getElement(), "position", "relative");
      DOM.setStyleAttribute(grid.getElement(), "left", "0px");
      DOM.setStyleAttribute(grid.getElement(), "top", "0px");

      buttonsPanel = new Toolbar();
      //buttonsPanel.setSize("100%", "32px");
      //DOM.setStyleAttribute(buttonsPanel.getElement(), "background", "#EEFF00");
      grid.setWidget(0, 0, buttonsPanel);
      DOM.setStyleAttribute(grid.getRowFormatter().getElement(0), "height", "32px");

      FlowPanel tabWrapper = new FlowPanel();
      tabWrapper.setSize("100%", "100%");
      DOM.setStyleAttribute(buttonsPanel.getElement(), "background", "#00FFAA");
      grid.setWidget(1, 0, tabWrapper);

      testCasePanel().add(grid);

      tabPanel = new TabPanel();
      tabPanel.setSize("400px", "300px");
      tabWrapper.add(tabPanel);

      createButtons();
   }

   private void createButtons()
   {
      Image addNormal = new Image(ShowCaseImageBundle.INSTANCE.add());
      Image addDisabled = new Image(ShowCaseImageBundle.INSTANCE.addDisabled());
      IconButton addTabButton = new IconButton(addNormal, addDisabled);

      buttonsPanel.addItem(addTabButton);

      addTabButton.addClickHandler(addTabClickHandler);

      Image removeNormal = new Image(ShowCaseImageBundle.INSTANCE.remove());
      Image removeDisabled = new Image(ShowCaseImageBundle.INSTANCE.removeDisabled());
      IconButton removeTabButton = new IconButton(removeNormal, removeDisabled);

      buttonsPanel.addItem(removeTabButton);

      removeTabButton.addClickHandler(removeTabClickHandler);

      Image control1Normal = new Image(ShowCaseImageBundle.INSTANCE.ok());
      Image control1Disabled = new Image(ShowCaseImageBundle.INSTANCE.okDisabled());
      IconButton control1TabButton = new IconButton(control1Normal, control1Disabled);
      buttonsPanel.addItem(control1TabButton);
      control1TabButton.addClickHandler(control1ClickHandler);

      Image control2Normal = new Image(ShowCaseImageBundle.INSTANCE.cancel());
      Image control2Disabled = new Image(ShowCaseImageBundle.INSTANCE.cancelDisabled());
      IconButton control2TabButton = new IconButton(control2Normal, control2Disabled);
      buttonsPanel.addItem(control2TabButton);
      control2TabButton.addClickHandler(control2ClickHandler);

   }

   static int nextTabId = 1;

   private ClickHandler addTabClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         String tabId = "tab-" + nextTabId;

         Image icon = new Image(ShowCaseImageBundle.INSTANCE.search());

         Border border = new Border();
         
         Image widget = new Image("exo.png");
         border.add(widget);

         tabPanel.addTab(tabId, icon, "Tab " + nextTabId, new Label(tabId), true);

         tabPanel.selectTab(tabId);

         nextTabId++;
      }
   };

   private ClickHandler removeTabClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         int selectedTabIndex = tabPanel.getSelectedTab();
         String selectedTabId = tabPanel.getTabIdByIndex(selectedTabIndex);

         tabPanel.removeTab(selectedTabId);

      }
   };

   private List<TabButton> tabControls = new ArrayList<TabButton>();

   private ClickHandler control1ClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         Image image = new Image(ShowCaseImageBundle.INSTANCE.search());
         Image disabledImage = new Image(ShowCaseImageBundle.INSTANCE.searchDisabled());
         TabButton control = new TabButton(image, disabledImage);

         tabPanel.addTabButton(control);

         tabControls.add(control);
      }
   };

   private ClickHandler control2ClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         if (tabControls.size() == 0)
         {
            return;
         }

         TabButton control = tabControls.get(0);
         tabControls.remove(0);

         tabPanel.removeTabButton(control);
      }
   };

}