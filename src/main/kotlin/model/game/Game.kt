package model.game

import java.util.*
import java.util.concurrent.TimeUnit

data class GameSettings(val title: String, val wordsCount: Int, val playersCount: Int)

enum class PlayerState {
    SelectingWords, ReadyToPlay
}

enum class GameState {
    GatheringParty, Playing
}

data class Player(val id: String, val name: String, val words: Set<String>, val state: PlayerState)

data class Team(val explainerId: String, val listenerId: String) {
    fun reverse(): Team {
        return copy(explainerId = listenerId, listenerId = explainerId)
    }
}

data class Game(
    val id: String,
    val settings: GameSettings,
    val state: GameState,
    val players: Map<String, Player>,
    val deck: Set<String>,
    val teams: List<Team>,
    val currentTeam: Int) {

    fun setPlayerWords(playerId: String, newWords: Set<String>): Game {
        if (settings.wordsCount != newWords.size)
            throw IllegalArgumentException("Wrong number of words (${newWords.size} instead of ${settings.wordsCount} expected).")
        return updatePlayer(playerId) {
            it.copy(words = newWords.toSet(), state = PlayerState.ReadyToPlay)
        }.startIfReady()
    }

    fun setDeck(playerId: String, newDeck: Set<String>): Game {
        val currentPlayer = players[playerId]!!
        if (teams[currentTeam].explainerId != currentPlayer.id)
            throw java.lang.IllegalArgumentException("Current player not explainer!")

        return copy(deck = newDeck)
    }

    fun joinGame(player: Player): Game {
        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500))
        if (state != GameState.GatheringParty)
            throw IllegalArgumentException("Game already started.")
        if (players.size >= settings.playersCount)
            throw IllegalArgumentException("Game already full of players.")
        val players = players + Pair(player.id, player)
        return copy(players = players)
    }

    private fun startIfReady(): Game {
        return if (readyToStart()) startGame() else this
    }

    private fun updatePlayer(playerId: String, playerUpdate: (player: Player) -> Player): Game {
        val currentPlayer = players[playerId]
        if (currentPlayer != null) {
            val newPlayer = playerUpdate(currentPlayer)
            val players = players + Pair(playerId, newPlayer)
            return copy(players = players)
        }
        return this
    }

    private fun readyToStart(): Boolean {
        return settings.playersCount == players.values.size
                && players.values.all { it.state == PlayerState.ReadyToPlay }
    }

    private fun startGame(): Game {
        val players = players.values
        val deck = players.fold(emptyList<String>()) { acc, player ->  acc + player.words}.toSet()
        val straightTeams = players.shuffled().chunked(2) { twoPlayers -> Team(twoPlayers[0].id, twoPlayers[1].id) }
        val reversedTeams = straightTeams.map(Team::reverse)
        val teams = straightTeams + reversedTeams
        return copy(state = GameState.Playing, deck = deck, teams = teams)
    }
}

fun newId(): String {
    return UUID.randomUUID().toString()
}

fun newGame(settings: GameSettings): Game {
    if (settings.playersCount % 2 != 0)
        throw IllegalArgumentException("There should be even number of players.")
    val id = newId()
    return Game(id, settings, GameState.GatheringParty, emptyMap(), emptySet(), emptyList(), 0)
}

fun newPlayer(name: String): Player {
    return Player(newId(), name, emptySet(), PlayerState.SelectingWords)
}