import io.javalin.Javalin
import io.javalin.websocket.WsContext
import j2html.TagCreator.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import game.*

import com.google.gson.Gson

private val userUsernameMap = ConcurrentHashMap<WsContext, String>()
private var nextUserNumber = 1 // Assign to username for next connecting user

fun main(args: Array<String>) {
    val fakeSettings = GameSettings("Test", 7, 6)
    println(fakeSettings)
    val gson = Gson()
    val user = User("Vasya")
    user.ready()
    val game = Game(fakeSettings)
    game.addUser(user)
    println(gson.toJson(game))

    return
    Javalin.create {
        it.addStaticFiles("/public")
    }.apply {
        ws("/chat") { ws ->
            ws.onConnect { ctx ->
                val username = "User" + nextUserNumber++
                userUsernameMap.put(ctx, username)
                broadcastMessage("Server", "$username joined the chat")
            }
            ws.onClose { ctx ->
                val username = userUsernameMap[ctx]
                userUsernameMap.remove(ctx)
                broadcastMessage("Server", "$username left the chat")
            }
            ws.onMessage { ctx ->
                broadcastMessage(userUsernameMap[ctx]!!, ctx.message())
            }
        }
    }.start(7070)
}

// Sends a message from one user to all users, along with a list of current usernames
fun broadcastMessage(sender: String, message: String) {
    userUsernameMap.keys.filter { it.session.isOpen }.forEach { session ->
        session.send(
            JSONObject()
                .put("userMessage", createHtmlMessageFromSender(sender, message))
                .put("userlist", userUsernameMap.values).toString()
        )
    }
}

// Builds a HTML element with a sender-name, a message, and a timestamp,
private fun createHtmlMessageFromSender(sender: String, message: String): String {
    return article(
        b("$sender says:"),
        span(attrs(".timestamp"), SimpleDateFormat("HH:mm:ss").format(Date())),
        p(message)
    ).render()
}