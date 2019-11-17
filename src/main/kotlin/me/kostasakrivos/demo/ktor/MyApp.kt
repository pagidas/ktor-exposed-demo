package me.kostasakrivos.demo.ktor

import Database
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.WebSockets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.kostasakrivos.demo.ktor.api.userResource
import me.kostasakrivos.demo.ktor.config.ConfigReader
import me.kostasakrivos.demo.ktor.repository.UserRepositoryImpl
import me.kostasakrivos.demo.ktor.service.UserService

@ExperimentalCoroutinesApi fun Application.module() {
    Database.setPersistence()
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        jackson()
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)
    install(Routing) {
        userResource(UserService(UserRepositoryImpl()))
    }
}

@ExperimentalCoroutinesApi fun main() {
    val serverConfig = ConfigReader.server()
    embeddedServer(Netty, serverConfig.port, serverConfig.host, watchPaths = listOf("MyAppKt"), module = Application::module).start(wait = true)
}