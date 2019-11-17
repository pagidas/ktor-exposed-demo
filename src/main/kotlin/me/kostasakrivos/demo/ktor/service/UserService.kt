package me.kostasakrivos.demo.ktor.service

import me.kostasakrivos.demo.ktor.model.ChangeType
import me.kostasakrivos.demo.ktor.model.Notification
import me.kostasakrivos.demo.ktor.model.User
import me.kostasakrivos.demo.ktor.repository.UserRepository

class UserService(private val userRepository: UserRepository) {

    private val listeners = mutableMapOf<Int, suspend (Notification<User?>) -> Unit>()

    suspend fun saveUser(user: User): User {
        return userRepository.insertUser(user).let {
            onChange(ChangeType.CREATE, it.id!!, it)
            it
        }
    }

    suspend fun findAllUsers(): MutableSet<User> {
        return userRepository.getAllUsers()
    }

    suspend fun findUser(userId: Int): User? {
        return userRepository.getUserById(userId)
    }

    suspend fun removeUser(userId: Int): Boolean {
        return userRepository.deleteUser(userId).also { if (it) onChange(ChangeType.DELETE, userId) }
    }

    suspend fun editUser(user: User): User? {
        return userRepository.updateUser(user).also { onChange(ChangeType.UPDATE, it!!.id!!, it) }
    }

    fun addChangeListener(id: Int, listener: suspend (Notification<User?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: User? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }
}