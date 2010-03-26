/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.discovery;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ideall.client.model.discovery.marshal.EntryPointListUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class DiscoveryServiceImpl extends DiscoveryService
{

   private static final String CONTEXT = "/services/discovery/entrypoints";

   private HandlerManager eventBus;

   public DiscoveryServiceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   @Override
   public void getEntryPoints()
   {
      String url = Configuration.getInstance().getContext() + CONTEXT;

      List<String> entryPoints = new ArrayList<String>();

      EntryPointsReceivedEvent event = new EntryPointsReceivedEvent(entryPoints);
      EntryPointListUnmarshaller unmarshaller = new EntryPointListUnmarshaller(entryPoints);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

}
