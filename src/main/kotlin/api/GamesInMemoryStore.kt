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
        val player = model.game.newPlayer(name)
        val updatedGame = updateGameWithLock(gameId) { it.joinGame(player) }
        return GameContext(updatedGame, player.id)
    }

    fun setWords(gameId: String, playerId: String, newWords: Set<String>): GameContext {
        val updatedGame = updateGameWithLock(gameId) { it.setPlayerWords(playerId, newWords) }
        return GameContext(updatedGame, playerId)
    }

    private fun updateGameWithLock(gameId: String, updateFn: (game: Game) -> Game): Game {
        val gameLock = gameLocks[gameId] ?: throw NotFoundResponse()
        return gameLock.withLock {
            val game = games[gameId] ?: throw NotFoundResponse()
            val updatedGame = updateFn(game)
            games[game.id] = updatedGame
            updatedGame
        }
    }
}