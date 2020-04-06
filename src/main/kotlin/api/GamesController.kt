package api

import game.Game
import game.GameSettings
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse

val games = mutableSetOf(
    Game(GameSettings("Тестовая", 7, 6))
)

object GamesController {
    fun getAll(ctx: Context) {
        ctx.json(games)
    }

    fun getOne(ctx: Context) {
        val game = games.find { it.id == ctx.pathParam("game-id") } ?: throw NotFoundResponse()
        ctx.json(game)
    }

    fun createGame(ctx: Context) {
        ctx.json({})
    }
}