# Byte Bistro
Simple Spring Boot application that demonstrates 
* OIDC using Keycloak
* Authentication and authorization using Spring Security
* Multi-tenancy by implementing Hibernate's CurrentTenantIdentifierResolver interface

## Functionality
Currently, Byte Bistro only creates and reads menu items. Again, the focus is
on OIDC with Spring Security and Keycloak, not specifically CRUD operations.

## Steps to run locally
* Clone the repo
* Start the Keycloak and Postgres containers
  * Run `docker-compose up -d` in the project root directory
  * `docker-compose.yml` is configured to use environment variables, but will use the default if variables are not found
  * For demo purposes, the default Keycloak credentials are
    * username: `user`
    * password: `bitnami`
* Create realms in Keycloak. These will translate to tenants in the application
* Register clients for each realm, and create users for each client
* Create "Realm Roles" for each user. 
  * The application only uses three roles
    * kitchen.admin
    * create-menu
    * read-menu
