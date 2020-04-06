package game

import java.util.*

data class GameSettings(val title: String, val wordsCount: Int, val personsCount: Int)

enum class UserStatus {
    SelectingWords, ReadyToJoin
}

class User(val name: String) {
    val id = UUID.randomUUID().toString()
    val words: Set<String> = mutableSetOf()
    var status = UserStatus.SelectingWords

    fun ready() {
        status = UserStatus.ReadyToJoin
    }
}

class Game(val settings: GameSettings) {
    val id = UUID.randomUUID().toString()
    val users: MutableSet<User> = mutableSetOf()

    init {
        if (settings.personsCount % 2 != 0)
            throw IllegalArgumentException("There should be even number of persons.")
    }

    fun addUser(user: User) {
        if (users.size >= settings.personsCount)
            throw Exception("Too many users for game.")
        users.add(user)
    }
}