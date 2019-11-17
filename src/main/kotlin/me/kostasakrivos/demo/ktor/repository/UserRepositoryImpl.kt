package me.kostasakrivos.demo.ktor.repository

import Database.queryDb
import me.kostasakrivos.demo.ktor.model.User
import me.kostasakrivos.demo.ktor.model.Users
import org.jetbrains.exposed.sql.*

class UserRepositoryImpl: UserRepository {

    override suspend fun getUserById(userId: Int): User? = queryDb {
        Users.select {
            Users.id eq userId
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    override suspend fun getUserByName(name: String): User? {
        TODO("Implement me!")
    }

    override suspend fun insertUser(user: User): User {
        var userId = 0
        queryDb {
            val id = Users.insert {
                it[name] = user.name
            } get Users.id
            userId = checkNotNull(id.value, { "Insert for user $user has not been successful" })
        }
        return getUserById(userId)!!
    }

    override suspend fun getAllUsers() = queryDb {
        Users.selectAll().mapNotNull(::toUser).toMutableSet()
    }

    override suspend fun deleteUser(userId: Int): Boolean {
        return queryDb {
            Users.deleteWhere { Users.id eq userId } > 0
        }
    }

    override suspend fun updateUser(user: User): User? {
        val id = user.id
        return if (id == null) {
            insertUser(user)
        } else {
            queryDb {
                Users.update({ Users.id eq id }) {
                    it[name] = user.name
                }
            }
            getUserById(id)
        }
    }

    private fun toUser(row: ResultRow): User =
        User (
            id = row[Users.id].value,
            name = row[Users.name]
        )

}
