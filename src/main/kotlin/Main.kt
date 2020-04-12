import api.GamesController
import api.GamesInMemoryStore
import api.WebSocketController
import io.javalin.Javalin
import io.javalin.websocket.WsContext
import j2html.TagCreator.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.JavalinVue
import io.javalin.plugin.rendering.vue.VueComponent
import model.game.GameSettings

// import com.google.gson.Gson


fun main(args: Array<String>) {
    // add test game for debug
    val g = GamesInMemoryStore.newGame(GameSettings("Тестовая", 2, 4))
    val vasyaId = GamesInMemoryStore.joinGame(g.id, "vasya").playerId
    GamesInMemoryStore.setWords(g.id, vasyaId, setOf("hello", "world"))
    val mashaId = GamesInMemoryStore.joinGame(g.id, "masha").playerId
    GamesInMemoryStore.setWords(g.id, mashaId, setOf("привет", "стул"))
    val sashaId = GamesInMemoryStore.joinGame(g.id, "sasha").playerId
    GamesInMemoryStore.setWords(g.id, sashaId, setOf("лампа", "закат"))
    // start web server
    val app = Javalin.create {
        it.enableWebjars()
        // it.addStaticFiles("/public")
    }.ws("/games/:game-id/:player-id") { ws ->
        ws.onConnect(WebSocketController::onConnect)
        ws.onClose(WebSocketController::onClose)
        ws.onMessage(WebSocketController::onMessage)
    }.start(7070)

    JavalinVue.stateFunction = GamesController::stateFunction

    app.routes {
        get(VueComponent("games"))
        path("games") {
            path(":game-id") {
                get(VueComponent("game"))
                path(":player-id") {
                    get(GamesController::gameAndPlayer)
                }
            }
        }
        path("api") {
            path("games") {
                get(GamesController::getAll)
                post(GamesController::createGame)
                path(":game-id") {
                    get(GamesController::getOne)
                    path("join") {
                        post(GamesController::joinGame)
                    }
                    path(":player-id") {
                        path("setWords") {
                            post(GamesController::setWords)
                        }
                    }
                }
            }
        }
    }
}