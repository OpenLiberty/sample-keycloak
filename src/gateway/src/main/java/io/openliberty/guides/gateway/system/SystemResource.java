/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.guides.gateway.system;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("/system/properties")
public class SystemResource {

    @Inject
    @RestClient
    private SystemService systemService;

    @Inject
    private OpenIdContext openIdContext;

    @GET
    @Path("/username")
    @RolesAllowed({ "admin" })
    public String getUsername() {
        return systemService.getUsername(openIdContext.getAccessToken().getToken());
    }

    @GET
    @Path("/os")
    @RolesAllowed({ "admin", "user" })
    public String getOS() {
        return systemService.getOS(openIdContext.getAccessToken().getToken());
    }
    
}
