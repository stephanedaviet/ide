/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RepoInfoUnmarshallerWS.java Nov 21, 2012 3:02:52 PM azatsarynnyy $
 */
public class RepoInfoUnmarshallerWS implements Unmarshallable<RepoInfo> {
    private final DtoClientImpls.RepoInfoImpl repoInfo;

    public RepoInfoUnmarshallerWS(DtoClientImpls.RepoInfoImpl repoInfo) {
        this.repoInfo = repoInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseLenient(response.getBody()).isObject();
        String value = jsonObject.toString();
        DtoClientImpls.RepoInfoImpl repoInfo = DtoClientImpls.RepoInfoImpl.deserialize(value);
        this.repoInfo.setRemoteUri(repoInfo.getRemoteUri());
    }

    /** {@inheritDoc} */
    @Override
    public RepoInfo getPayload() {
        return repoInfo;
    }
}