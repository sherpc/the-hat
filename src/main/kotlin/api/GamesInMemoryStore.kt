package api

import io.javalin.http.NotFoundResponse
import model.game.Game
import model.game.GameSettings


object GamesInMemoryStore {
    private val testGame = model.game.newGame(GameSettings("Тестовая", 7, 6))
    private val games = mutableMapOf(testGame.id to testGame)

    fun allGames(): Map<String, Game> {
        return games
    }

    fun byId(id: String): Game? {
        return games[id]
    }

    fun newGame(settings: GameSettings): Game {
        val game = model.game.newGame(settings)
        games[game.id] = game
        return game
    }

    fun joinGame(gameId: String, name: String): GameContext {
        val game = games[gameId] ?: throw NotFoundResponse()
        val player = model.game.newPlayer(name)
        val updatedGame = model.game.joinGame(game, player)
        games[game.id] = updatedGame
        return GameContext(updatedGame, player.id)
    }
}