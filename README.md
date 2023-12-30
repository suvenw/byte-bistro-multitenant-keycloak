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
* Stand up a Keycloak server
  * Running Keycloak in a Docker container is an easy way to spin up a Keycloak server
* Create realms in Keycloak. These will translate to tenants in the application
* Register clients for each realm, and create users for each client
* Create "Realm Roles" for each user. 
  * The application only uses three roles
    * kitchen.admin
    * create-menu
    * read-menu
* Have a Postgresql database running with the database and schema defined in application.yml
