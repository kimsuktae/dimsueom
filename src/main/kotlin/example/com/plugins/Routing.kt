package example.com.plugins

import example.com.plugins.member.MemberService
import example.com.plugins.sse.triggerSSE
import example.com.plugins.team.TeamService
import example.com.views.lunch.li
import example.com.views.lunch.lunch
import example.com.views.lunch.member
import example.com.views.lunch.memberList
import example.com.views.lunch.teamList
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRouting(
    memberService: MemberService,
    teamService: TeamService,
) {
    routing {
        staticResources("/static", "static")

        get("/") {
            call.respondHtml {
                lunch()
            }
        }

        route("/members") {
            get {
                val uid = call.request.queryParameters["uid"] ?: throw BadRequestException("no uid")
                val members = memberService.getAllMembers()

                call.respondHtml {
                    body {
                        memberList(members, uid)
                    }
                }
            }

            put("/{name}") {
                val uid = call.request.queryParameters["uid"] ?: throw BadRequestException("no uid")
                val name = call.parameters["name"] ?: throw BadRequestException("no name")

                val updated = memberService.updateMember(name)

                call.respondHtml {
                    body {
                        li("member-item") {
                            member(updated, uid)
                        }
                    }
                }

                triggerSSE(uid)
            }
        }

        get("/team") {
            val team = teamService.getTeam()

            println(team)
            call.respondHtml {
                body {
                    teamList(team)
                }
            }
        }
    }
}
