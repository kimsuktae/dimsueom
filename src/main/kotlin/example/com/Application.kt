package example.com

import example.com.plugins.*
import example.com.plugins.member.MemberService
import example.com.plugins.sse.configureSse
import example.com.plugins.team.TeamService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.CORS

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val memberService = MemberService()
    val teamService = TeamService(memberService)
    configureRouting(memberService, teamService)
    configureSse()
    configureCors()
}

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        anyHost()
    }
}

