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

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8081/system")
@Path("/properties")
public interface SystemService {

    @GET
    @Path("/username")
    public String getUsername(@HeaderParam("Authorization") String bearerToken);
       
    @GET
    @Path("/os")
    public String getOS(@HeaderParam("Authorization") String bearerToken);

}
