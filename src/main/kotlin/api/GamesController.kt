package api

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.rendering.vue.VueComponent
import model.game.Game
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
        updateGameAndBroadcast(ctx) {
            GamesInMemoryStore.joinGame(gameIdFromPath(ctx), name)
        }
    }

    fun setWords(ctx: Context) {
        val gameContext = gameContextFromPath(ctx)
        val words = ctx.body<Set<String>>()
        updateGameAndBroadcast(ctx) {
            GamesInMemoryStore.setWords(gameContext.game.id, gameContext.playerId, words)
        }
    }

    fun remainingDeck(ctx: Context) {
        val gameContext = gameContextFromPath(ctx)
        val deck = ctx.body<Set<String>>()
        updateGameAndBroadcast(ctx) {
            GamesInMemoryStore.setDeck(gameContext.game.id, gameContext.playerId, deck)
        }
    }

    private fun updateGameAndBroadcast(ctx: Context, exceptPlayerId: String? = null, updateFn: () -> GameContext) {
        val statusCode = try {
            val updatedGameContext = updateFn()
            if (exceptPlayerId != null) {
                WebSocketController.broadcastToAllExcept(updatedGameContext.game, exceptPlayerId)
            } else {
                WebSocketController.broadcastGameState(updatedGameContext.game)
            }
            ctx.json(updatedGameContext)
            200
        } catch (e: Exception) {
            logger.error("Error while updating game.", e)
            500
        }
        ctx.status(statusCode)
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

