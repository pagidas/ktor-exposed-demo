package me.kostasakrivos.demo.ktor.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.receiveOrNull
import me.kostasakrivos.demo.ktor.model.User
import me.kostasakrivos.demo.ktor.service.UserService

@ExperimentalCoroutinesApi fun Route.userResource(userService: UserService) {

    route("/users") {

        get("/") {
            call.respond(userService.findAllUsers())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val user = userService.findUser(id)
            if (user == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(user)
        }

        post("/") {
            val request = call.receive<User>()
            call.respond(HttpStatusCode.Created, userService.saveUser(request))
        }

        put("/") {
            val user = call.receive<User>()
            val updated = userService.editUser(user)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val removed = userService.removeUser(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }

    val mapper = jacksonObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    webSocket("/updates") {
        try {
            userService.addChangeListener(this.hashCode()) {
                outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
            }
            while (true) {
                incoming.receiveOrNull() ?: break
            }
        } finally {
            userService.removeChangeListener(this.hashCode())
        }
    }
}
