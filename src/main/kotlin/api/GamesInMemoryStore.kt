package api

import io.javalin.http.NotFoundResponse
import model.game.Game
import model.game.GameSettings
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object GamesInMemoryStore {
    private val games = mutableMapOf<String, Game>()
    private val gameLocks = mutableMapOf<String, ReentrantLock>()

    fun allGames(): Map<String, Game> {
        return games
    }

    fun byId(id: String): Game? {
        return games[id]
    }

    fun newGame(settings: GameSettings): Game {
        val game = model.game.newGame(settings)
        games[game.id] = game
        gameLocks[game.id] = ReentrantLock()
        return game
    }

    fun joinGame(gameId: String, name: String): GameContext {
        return updateGameWithLock(gameId) {
            val game = games[gameId] ?: throw NotFoundResponse()
            val player = model.game.newPlayer(name)
            val updatedGame = model.game.joinGame(game, player)
            games[game.id] = updatedGame
            GameContext(updatedGame, player.id)
        }
    }

    private fun <T> updateGameWithLock(gameId: String, updateFn: () -> T): T {
        val gameLock = gameLocks[gameId] ?: throw NotFoundResponse()
        return gameLock.withLock { updateFn() }
    }
}