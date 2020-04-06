package api

import game.Game
import game.GameSettings
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import org.slf4j.LoggerFactory

val games = mutableSetOf(
    Game(GameSettings("Тестовая", 7, 6))
)

object GamesController {
    private val logger = LoggerFactory.getLogger(GamesController::class.java)

    fun getAll(ctx: Context) {
        ctx.json(games)
    }

    fun getOne(ctx: Context) {
        val game = games.find { it.id == ctx.pathParam("game-id") } ?: throw NotFoundResponse()
        ctx.json(game)
    }

    fun createGame(ctx: Context) {
        val settings = ctx.body<GameSettings>()
        val statusCode = try {
            val game = Game(settings)
            games.add(game)
            logger.info("Created game with id '${game.id}'.")
            201
        } catch (e: Exception) {
            logger.error("Error while creating game.", e)
            400
        }
        ctx.status(statusCode)
    }
}