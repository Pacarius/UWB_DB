package com.pacarius.routes
import com.pacarius.lampposts
import com.pacarius.main
import com.pacarius.vehicles
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.cordRouting(){
    routing{
        get("/lampposts") {
            call.respond(lampposts)
        }
        get("/vehicles") {
            call.respond(vehicles)
        }
    }
}