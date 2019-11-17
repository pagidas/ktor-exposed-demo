package api

import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import me.kostasakrivos.demo.ktor.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import server.ServerTest

class UserResourceTest: ServerTest() {

    @Test
    fun `can create a user`() {
        // when
        val newUser = User(null, "Kostas")
        val created = addUser(newUser)

        val retrieved = get("/users/{id}", created.id)
            .then()
            .statusCode(200)
            .extract().to<User>()

        // then
        assertThat(created.name).isEqualTo(newUser.name)
        assertThat(created).isEqualTo(retrieved)
    }

    @Test
    fun `can get all users`() {
        // when
        val user1 = User(null, "Kostas")
        val user2 = User(null, "Argy")
        addUser(user1)
        addUser(user2)

        val users = get("/users")
            .then()
            .statusCode(200)
            .extract().to<List<User>>()

        // then
        assertThat(users).hasSize(2)
        assertThat(users).extracting("name").containsExactlyInAnyOrder(user1.name, user2.name)
    }

    @Test
    fun `can get a certain user`() {
        // when
        val newUser = User(null, "Kostas")
        val created = addUser(newUser)

        val retrieved = get("/users/{id}", created.id)
            .then()
            .statusCode(200)
            .extract().to<User>()

        // then
        assertThat(retrieved).isEqualTo(created)
    }

    @Test
    fun `can delete a user`() {
        // when
        val newUser = User(null, "Kostas")
        val created = addUser(newUser)

        // then
        delete("/users/{id}", created.id)
            .then()
            .statusCode(200)

        get("/users/{id}", created.id)
            .then()
            .statusCode(404)
    }

    @Test
    fun `can update a user`() {
        // when
        val newUser = User(null, "Kostas")
        val created = addUser(newUser)

        // then
        val update = User(created.id, "Argy")
        val updatedUser = given()
            .contentType(ContentType.JSON)
            .body(update)
            .When()
            .put("/users")
            .then()
            .statusCode(200)
            .extract().to<User>()

        assertThat(updatedUser).isNotNull
        assertThat(updatedUser.id).isEqualTo(update.id)
        assertThat(updatedUser.name).isEqualTo(update.name)
    }

    private fun addUser(user: User): User {
        return given()
            .contentType(ContentType.JSON)
            .body(user)
            .When()
            .post("/users")
            .then()
            .statusCode(201)
            .extract().to()
    }
}