/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.dto;

import org.exoplatform.ide.dtogen.shared.RoutingType;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;
import org.exoplatform.ide.json.shared.JsonArray;
import org.exoplatform.ide.json.shared.JsonStringMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = RoutingTypes.GETOPENEDFILESINWORKSPACERESPONSE)
public interface GetOpenedFilesInWorkspaceResponse extends ServerToClientDto {
    /**
     * Key is file path, value array of users that open this file
     *
     * @return the JsonStringMap
     */
    JsonStringMap<JsonArray<ParticipantUserDetails>> getOpenedFiles();
}