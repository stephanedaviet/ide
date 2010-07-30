/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ideall.client.module.edit.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id: 
 */
public class FormatFileEvent extends GwtEvent<FormatFileHandler>
{

   public static final GwtEvent.Type<FormatFileHandler> TYPE = new GwtEvent.Type<FormatFileHandler>();

   @Override
   protected void dispatch(FormatFileHandler handler)
   {
      handler.onFormatFile(this);
   }

   @Override
   public Type<FormatFileHandler> getAssociatedType()
   {
      return TYPE;
   }
}
