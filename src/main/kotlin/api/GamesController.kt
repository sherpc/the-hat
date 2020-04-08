package api

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import model.game.GameSettings
import org.slf4j.LoggerFactory

val testGame = model.game.newGame(GameSettings("Тестовая", 7, 6))
val gamesInMemoryStore = mutableMapOf(testGame.id to testGame)

data class JoinGameDto(val gameId: String, val name: String)

object GamesController {
    private val logger = LoggerFactory.getLogger(GamesController::class.java)

    fun getAll(ctx: Context) {
        ctx.json(gamesInMemoryStore)
    }

    fun getOne(ctx: Context) {
        val game = gamesInMemoryStore.get(gameIdFromPath(ctx)) ?: throw NotFoundResponse()
        ctx.json(game)
    }

    fun createGame(ctx: Context) {
        val settings = ctx.body<GameSettings>()
        val statusCode = try {
            val game = model.game.newGame(settings)
            gamesInMemoryStore[game.id] = game
            logger.info("Created game with id '${game.id}'.")
            201
        } catch (e: Exception) {
            logger.error("Error while creating game.", e)
            400
        }
        ctx.status(statusCode)
    }

    fun joinGame(ctx: Context) {
        val name = ctx.body()
        val game = gamesInMemoryStore[gameIdFromPath(ctx)] ?: throw NotFoundResponse()
        val statusCode = try {
            gamesInMemoryStore[game.id] = model.game.joinGame(game, name)
            ctx.json(gamesInMemoryStore)
            200
        } catch (e: Exception) {
            logger.error("Error while creating game.", e)
            400
        }
        ctx.status(statusCode)
    }

    private fun gameIdFromPath(ctx: Context): String {
        return ctx.pathParam("game-id")
    }
}