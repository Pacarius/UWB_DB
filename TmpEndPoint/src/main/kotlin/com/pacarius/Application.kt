package com.pacarius

import com.pacarius.models.Coordinates
import com.pacarius.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val lampposts = mutableListOf<Coordinates>()
val vehicles = mutableListOf<Coordinates>()
fun main() {
    SqlConnector(lampposts, vehicles)
    embeddedServer(Netty, port = 9696, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    lampposts()
    vehicles()
}
fun Application.lampposts() {
    routing {
        get("/lampposts") {
            call.respond(lampposts)
        }
    }
}
fun Application.vehicles() {
    routing {
        get("/vehicles") {
            call.respond(vehicles)
        }
    }
}
