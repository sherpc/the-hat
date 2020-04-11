package api

import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import model.game.Game
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private data class PlayerContext(val gameId: String, val playerId: String)

object WebSocketController {
    private val userPlayerContextMap =
        ConcurrentHashMap<WsContext, PlayerContext>()

    private val logger = LoggerFactory.getLogger(WebSocketController::class.java)

    fun onConnect(ctx: WsContext) {
        val playerContext = PlayerContext(gameIdFromPath(ctx), playerIdFromPath(ctx))
        // TODO: add validation for playerContext
        userPlayerContextMap[ctx] = playerContext
    }

    fun onClose(ctx: WsContext) {
        userPlayerContextMap.remove(ctx)
    }

    fun onMessage(ctx: WsMessageContext) {
        val playerContext = userPlayerContextMap[ctx]
        logger.trace("message from ${playerContext}: '${ctx.message()}'")
    }

    fun broadcastGameState(game: Game) {
        userPlayerContextMap
            .filter { it.key.session.isOpen && it.value.gameId == game.id }
            .forEach { it.key.send(GameContext(game, it.value.playerId)) }
    }

    private fun gameIdFromPath(ctx: WsContext): String {
        return ctx.pathParam(Constants.GameIdPathKey)
    }

    private fun playerIdFromPath(ctx: WsContext): String {
        return ctx.pathParam(Constants.PlayerIdPathKey)
    }
}