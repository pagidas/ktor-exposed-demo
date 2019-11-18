## A demo RESTful web service in Kotlin

#### Main technologies used:
 - [Ktor](https://github.com/ktorio/ktor) - Kotlin asynch web framework
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin ORM SQL DLS framework
 
 This demo app creates an in-memory H2 database with one table which holds instances of `User` 
 and handles HTTP requests that implement the CRUD for that resource.
 
 #### Routes:
 
 `GET /users` gets all users from the database.
 
 `GET /users/{id}` gets one user by that id.
 
 `POST /users` creates a new user into the database.
 e.g.
 ```jshelllanguage
{
    "name": "Kostas"
}
```
and response
```jshelllanguage
{
    "id": 2,
    "name": "Kostas"
}
```
`PUT /users` updates an existing user in the database, should provide the id
of that user in request as JSON format.

`DELETE /users/{id}` deletes a user from the database by that id.

#### Testing

The demo provides integration testing calling the endpoints to assert the functionality using [Rest Assured](http://rest-assured.io/) framework.
 