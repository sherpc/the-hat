package api

import model.game.GameSettings

val testGame = model.game.newGame(GameSettings("Тестовая", 7, 6))
val gamesInMemoryStore = mutableMapOf(testGame.id to testGame)