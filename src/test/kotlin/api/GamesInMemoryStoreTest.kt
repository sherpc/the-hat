package api

import model.game.GameSettings
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GamesInMemoryStoreTest {
    @Test fun joinGameConcurrentTest() {
        val playersCount = 1000
        val game = GamesInMemoryStore.newGame(GameSettings("fake", 1, playersCount))
        assertEquals(1, GamesInMemoryStore.allGames().size)
        assertNotNull(game)
        runBlocking {
            withContext(Dispatchers.Default) {
                massiveRun(playersCount) {
                    GamesInMemoryStore.joinGame(game.id, "player")
                }
            }
        }
        val updatedGame = GamesInMemoryStore.byId(game.id)
        assertNotNull(updatedGame)
        assertEquals(playersCount, updatedGame.players.size)
    }

    suspend fun massiveRun(playersCount: Int, perCoroutine: Int = 2, action: suspend () -> Unit) {
        val n = playersCount / perCoroutine  // number of coroutines to launch
        val k = perCoroutine // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }
}