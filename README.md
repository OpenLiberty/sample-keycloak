# Sample App using Jakarta EE Security, MicroProfile JWT, and Keycloak

This sample app contains a `system` service which has a `GET /api/system/properties/username` endpoint, which returns the `user.name` system property, and a `GET /api/system/properties/os` endpoint, which returns the `os.name` system property. The endpoints are secured with JWT's using the `MicroProfile JWT 2.0` feature. Only users with the `admin` role can access the `GET /api/system/properties/username` endpoint, while users with either the `admin` or `user` role can access the `GET /api/system/properties/os` endpoint. The sample app also contains a `gateway` service whose responsibility is to obtain a JWT access token for the user from `Keycloak` and propagate it to the `system` service. This is accomplished using the `Jakarta EE Security 3.0` feature.

## Starting the application

### Start Keycloak

Run from the root directory:

```
docker build -t openliberty-keycloak -f keycloak/Dockerfile .
docker run --name openliberty-keycloak -p 8080:8080 -d openliberty-keycloak
```

### Start the System service

Run from the `src/system` directory:

```
mvn liberty:dev
```

### Start the Gateway service

In another shell instance, run from the `src/gateway` directory:

```
mvn liberty:dev
```

## Testing the application

### Users

| Username   | Password   | Role        |
| ---------- | ---------- | ----------- |
| alice      | alicepwd   | user        |
| bob        | bobpwd     | admin, user |

### Testing the username endpoint

Visit the username endpoint at the http://localhost:9090/api/system/properties/username URL. Log in as `bob` to view the `user.name` system property. Logging in as `alice` will result in a `403 Forbidden` response because only users with the `admin` role are allowed to access this endpoint.

Log out of the current user by visiting the logout endpoint at the http://localhost:9090/api/auth/logout URL.

### Testing the os endpoint

Visit the os endpoint at the http://localhost:9090/api/system/properties/os URL. Log in as `bob` or `alice` to view the `os.name` system property.

Log out of the current user by visiting the logout endpoint at the http://localhost:9090/api/auth/logout URL.

## How it works

![Token propagation diagram](/assets/diagram.png)

### Keycloak

Keycloak was used as the OpenId Connect Provider (OP) for this sample app. The `openliberty` realm was created in Keycloak. Inside this realm, the `sample-openliberty-keycloak` client was created which has the valid redirect URI's set to `http://localhost:9090/*`, client authentication enabled, and the `microprofile-jwt` client scope enabled. Additionally, the `admin` and `user` roles were created for this realm. Lastly, the `alice` user was created which has the `user` role and the `bob` user was created which has the `admin` role and the `user` role. Visit the http://localhost:8080/admin/master/console/#/openliberty URL and sign in using the `admin` username and `admin` password to view the `openliberty` realm.

The Keycloak OP's configuration can found at the http://localhost:8080/realms/openliberty/.well-known/openid-configuration URL.

### System Service

The `system` service secures its endpoints using MicroProfile JWT. By adding the `@RolesAllowed` annotation to a JAX-RS endpoint, the runtime will check `Authentication` HTTP request header for a Bearer token. The Bearer token must be in JWT format and include the specified role in the annotation in the JWT's `roles` claim in order to access the endpoint.

For the `/username` endpoint, it requires that the Bearer token's `roles` claim include the `admin` role.

```java
@GET
@Path("/username")
@RolesAllowed({ "admin" })
public String getUsername() {
    return systemService.getUsername(openIdContext.getAccessToken().getToken());
}
```

The following lines in the `microprofile-config.properties` file are required for MicroProfile JWT to validate that the Bearer token was issued from the correct source and has not been tampered with.

```properties
mp.jwt.verify.issuer=http://localhost:8080/realms/openliberty
mp.jwt.verify.publickey.location=http://localhost:8080/realms/openliberty/protocol/openid-connect/certs
```

The `mp.jwt.verify.issuer` should match the `issuer` value in the Keycloak OP config. The runtime will validate that the Bearer token's `iss` claim matches the `mp.jwt.verify.issuer` value. The `mp.jwt.verify.publickey.location` should match the `jwks_uri` value in the Keycloak OP config. The runtime will validate the Bearer token's signature with the public key found at the Keycloak OP's JSON Web Key (JWK) Set endpoint.

### Gateway Service

The `gateway` service is used to obtain a JWT access token from Keycloak and propagates it to the MicroProfile JWT secured `system` service.

For the `gateway` service, an `@OpenIdAuthenticationMechanismDefinition` is defined which has its `providerURI` set to the Keycloak OP's configuration URL, the `clientID` set to the `sample-openliberty-keycloak` client, and the `clientSecret` set to `sample-openliberty-keycloak`'s client secret. The `redirectToOriginalResource` is set to true, so that the browser will redirect to the original resource request after authentication. The `notifyProvider` is set to true, so that the browser will also redirect to the Keycloak OP's `end_session_endpoint` in the event of a logout. The `@DeclareRoles` annotation declares the `admin` role and the `user` role.

```java
@OpenIdAuthenticationMechanismDefinition(
        providerURI = "http://localhost:8080/realms/openliberty/.well-known/openid-configuration",
        clientId = "sample-openliberty-keycloak",
        clientSecret = "x4fRVAhk49TKDqVlzIt4q9oh8DSWfePt",
        redirectToOriginalResource = true,
        logout = @LogoutDefinition(notifyProvider = true))
@DeclareRoles({ "admin", "user" })
```

Now, adding the `@RolesAllowed` annotation to a JAX-RS endpoint will start a new OpenId Connect (OIDC) authorization code flow. The roles specified in the annotation for the `/username` endpoint in the `gateway` service should match the roles for the `/username` endpoint in the `system` service. If successful, an access token is obtained from the `OpenIdContext` and is propagated to the `system` service as a Bearer token in the `Authorization` header via MicroProfile Rest Client.

```java
@GET
@Path("/username")
@RolesAllowed({ "admin" })
public String getUsername() {
    return systemService.getUsername(openIdContext.getAccessToken().getToken());
}
```
