/*******************************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.guides.system;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.annotation.security.RolesAllowed;

@RequestScoped
@Path("/properties")
public class SystemResource {

    @GET
    @Path("/username")
    @RolesAllowed({ "admin" })
    public String getUsername() {
        return System.getProperties().getProperty("user.name");
    }

    @GET
    @Path("/os")
    @RolesAllowed({ "admin", "user" })
    public String getOS() {
        return System.getProperties().getProperty("os.name");
    }

}
