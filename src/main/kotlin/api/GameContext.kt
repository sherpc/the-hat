package api

import model.game.Game

data class GameContext(val game: Game, val playerId: String)