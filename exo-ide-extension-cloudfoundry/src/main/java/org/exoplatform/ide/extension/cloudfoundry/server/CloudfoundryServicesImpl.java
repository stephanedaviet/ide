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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;

import java.util.Arrays;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryServicesImpl implements CloudfoundryServices {
    private SystemService[]      system;
    private ProvisionedService[] provisioned;

    public CloudfoundryServicesImpl(SystemService[] system, ProvisionedService[] provisioned) {
        this.system = system;
        this.provisioned = provisioned;
    }

    public CloudfoundryServicesImpl() {
    }

    @Override
    public SystemService[] getSystem() {
        return system;
    }

    @Override
    public void setSystem(SystemService[] system) {
        this.system = system;
    }

    @Override
    public ProvisionedService[] getProvisioned() {
        return provisioned;
    }

    @Override
    public void setProvisioned(ProvisionedService[] provisioned) {
        this.provisioned = provisioned;
    }

    @Override
    public String toString() {
        return "CloudfoundryServicesImpl{" +
               "system=" + (system == null ? null : Arrays.asList(system)) +
               ", provisioned=" + (provisioned == null ? null : Arrays.asList(provisioned)) +
               '}';
    }
}