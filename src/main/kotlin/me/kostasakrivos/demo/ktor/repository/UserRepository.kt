package me.kostasakrivos.demo.ktor.repository

import me.kostasakrivos.demo.ktor.model.User

interface UserRepository {

    suspend fun getUserById(userId: Int): User?

    suspend fun insertUser(user: User): User

    suspend fun getAllUsers(): MutableSet<User>

    suspend fun deleteUser(userId: Int): Boolean

    suspend fun updateUser(user: User): User?
}
