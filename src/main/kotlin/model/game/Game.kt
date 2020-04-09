package model.game

import java.util.*

data class GameSettings(val title: String, val wordsCount: Int, val personsCount: Int)

enum class PlayerState {
    SelectingWords, ReadyToJoin
}

enum class GameState {
    GatheringParty, BuildingDeck, Playing
}

data class Player(val id: String, val name: String, val words: Set<String>, val state: PlayerState)

data class Game(
    val id: String,
    val settings: GameSettings,
    val state: GameState,
    val players: Map<String, Player>)

fun newId(): String {
    return UUID.randomUUID().toString()
}

fun newGame(settings: GameSettings): Game {
    if (settings.personsCount % 2 != 0)
        throw IllegalArgumentException("There should be even number of persons.")
    val id = newId()
    return Game(id, settings, GameState.GatheringParty, emptyMap())
}

fun newPlayer(name: String): Player {
    return Player(newId(), name, emptySet(), PlayerState.SelectingWords)
}

fun joinGame(game: Game, player: Player): Game {
    if (game.state != GameState.GatheringParty)
        throw IllegalArgumentException("Game already started.")
    if (game.players.size >= game.settings.personsCount)
        throw IllegalArgumentException("Game already full of players.")
    val players = game.players + Pair(player.id, player)
    val newGameState = if (players.size == game.settings.personsCount) GameState.BuildingDeck else game.state
    return game.copy(players = players, state = newGameState)
}