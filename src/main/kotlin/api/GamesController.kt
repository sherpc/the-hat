package api

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.rendering.vue.VueComponent
import model.game.GameSettings
import org.slf4j.LoggerFactory

object GamesController {
    private val logger = LoggerFactory.getLogger(GamesController::class.java)
    private const val SharedStateAttributeKey = "SharedStateAttributeKey"

    fun getAll(ctx: Context) {
        ctx.json(GamesInMemoryStore.allGames())
    }

    fun getOne(ctx: Context) {
        val game = GamesInMemoryStore.byId(gameIdFromPath(ctx)) ?: throw NotFoundResponse()
        ctx.json(game)
    }

    fun gameAndPlayer(ctx: Context) {
        val gameContext = gameContextFromPath(ctx)
        ctx.attribute(SharedStateAttributeKey, mapOf("gameContext" to gameContext))
        VueComponent("game").handle(ctx)
    }

    fun createGame(ctx: Context) {
        val settings = ctx.body<GameSettings>()
        val statusCode = try {
            val game = GamesInMemoryStore.newGame(settings)
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
        val statusCode = try {
            val updatedGameContext = GamesInMemoryStore.joinGame(gameIdFromPath(ctx), name)
            WebSocketController.broadcastGameState(updatedGameContext.game)
            ctx.json(updatedGameContext)
            200
        } catch (e: Exception) {
            logger.error("Error while creating game.", e)
            400
        }
        ctx.status(statusCode)
    }

    fun setWords(ctx: Context) {
        val gameContext = gameContextFromPath(ctx)
    }

    fun stateFunction(ctx: Context): Map<String, Any> {
        val state = ctx.attribute<Map<String, Any>>(SharedStateAttributeKey) ?: emptyMap()
        logger.trace("State function {}", state)
        return state
    }

    private fun gameIdFromPath(ctx: Context): String {
        return ctx.pathParam(Constants.GameIdPathKey)
    }

    private fun playerIdFromPath(ctx: Context): String {
        return ctx.pathParam(Constants.PlayerIdPathKey)
    }

    private fun gameContextFromPath(ctx: Context): GameContext {
        val game = GamesInMemoryStore.byId(gameIdFromPath(ctx)) ?: throw NotFoundResponse()
        val player = game.players[playerIdFromPath(ctx)] ?: throw NotFoundResponse()
        return GameContext(game, player.id)
    }
}

