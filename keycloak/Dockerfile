FROM quay.io/keycloak/keycloak:20.0.3

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

COPY keycloak/openliberty-realm.json /opt/keycloak/data/import/openliberty-realm.json

CMD ["start-dev", "--import-realm"]