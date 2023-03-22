/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.guides.gateway.auth;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.OpenIdProviderMetadata;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Path("/auth")
@OpenIdAuthenticationMechanismDefinition(
        providerURI = "http://localhost:8080/realms/openliberty/.well-known/openid-configuration",
        clientId = "sample-openliberty-keycloak",
        clientSecret = "x4fRVAhk49TKDqVlzIt4q9oh8DSWfePt",
        redirectToOriginalResource = true,
        logout = @LogoutDefinition(notifyProvider = true))
@DeclareRoles({ "admin", "user" })
public class AuthResource {

    @Context
    private HttpServletRequest httpServletRequest;

    @GET
    @Path("/logout")
    public void logout() throws Exception {
        httpServletRequest.logout();
    }
    
}
