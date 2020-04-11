import api.GamesController
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

// import com.google.gson.Gson


fun main(args: Array<String>) {
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
                }
            }
        }
    }
}