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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.StringValueReceivedHandler;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.SshService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerPresenter May 18, 2011 10:16:44 AM evgen $
 *
 */
public class SshKeyManagerPresenter implements ShowSshKeyManagerHandler, ViewClosedHandler
{
   public interface Display extends IsView
   {
      String ID = "ideSshKeyManagerView";

      HasSshGrid<KeyItem> getKeyItemGrid();

      HasClickHandlers getCloseButton();

   }

   private Display display;

   /**
   * 
   */
   public SshKeyManagerPresenter()
   {
      IDE.EVENT_BUS.addHandler(ShowSshKeyManagerEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler#onShowSshKeyManager(org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent)
    */
   @Override
   public void onShowSshKeyManager(ShowSshKeyManagerEvent event)
   {
      if (display != null)
         return;

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
      SshService.get().getAllKeys(new AsyncRequestCallback<List<KeyItem>>()
      {

         @Override
         protected void onSuccess(List<KeyItem> result)
         {
            display.getKeyItemGrid().setValue(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * 
    */
   private void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getKeyItemGrid().addViewButtonSelectionHandler(new SelectionHandler<KeyItem>()
      {

         @Override
         public void onSelection(SelectionEvent<KeyItem> event)
         {
            System.out.println("Show public key for host: " + event.getSelectedItem().getHost());
         }
      });

      display.getKeyItemGrid().addDeleteButtonSelectionHandler(new SelectionHandler<KeyItem>()
      {

         @Override
         public void onSelection(SelectionEvent<KeyItem> event)
         {
            System.out.println("Delete key for host: " + event.getSelectedItem().getHost());
         }
      });
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

}
