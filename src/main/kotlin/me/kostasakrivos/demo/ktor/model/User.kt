package me.kostasakrivos.demo.ktor.model

import org.jetbrains.exposed.dao.IntIdTable

data class User(
    val id: Int?,
    val name: String
)

// Declares objects which will be created as tables by exposed
object Users : IntIdTable() {
    val name = varchar("name", 50)
}

