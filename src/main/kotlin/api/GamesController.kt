package api

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.rendering.vue.VueComponent
import model.game.GameSettings
import org.slf4j.LoggerFactory

val testGame = model.game.newGame(GameSettings("Тестовая", 7, 6))
val gamesInMemoryStore = mutableMapOf(testGame.id to testGame)

const val SharedStateAttributeKey = "SharedStateAttributeKey"

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

    fun gameAndPlayer(ctx: Context) {
        val game = gamesInMemoryStore.get(gameIdFromPath(ctx)) ?: throw NotFoundResponse()
        val playerId = ctx.pathParam("player-id")
        val player = game.players.get(playerId) ?: throw NotFoundResponse()
        ctx.attribute(SharedStateAttributeKey, mapOf("game" to game, "player" to player))
        VueComponent("game").handle(ctx)
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
            val player = model.game.newPlayer(name)
            gamesInMemoryStore[game.id] = model.game.joinGame(game, player)
            ctx.json(player)
            200
        } catch (e: Exception) {
            logger.error("Error while creating game.", e)
            400
        }
        ctx.status(statusCode)
    }

    fun stateFunction(ctx: Context): Map<String, Any> {
        val state = ctx.attribute<Map<String, Any>>(SharedStateAttributeKey) ?: emptyMap()
        logger.trace("State function {}", state)
        return state
    }

    private fun gameIdFromPath(ctx: Context): String {
        return ctx.pathParam("game-id")
    }
}

