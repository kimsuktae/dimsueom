package example.com.views.lunch

import example.com.plugins.member.Member
import example.com.plugins.team.Team
import example.com.views.layout.layout
import java.util.UUID
import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.LI
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul
import kotlinx.html.visit

fun HTML.lunch() = layout {
    val uid = UUID.randomUUID()
    attributes["hx-ext"] = "sse"
    attributes["sse-connect"] = "/sse?uid=$uid"

    h1 { +"HT beyond Lunch Server" }

    div {
        ul {
            id = "list"

            attributes["hx-get"] = "/members?uid=$uid"
            attributes["hx-trigger"] = "load,sse:memberUpdated"

            p(classes = "htmx-indicator") { +"Loading..." }
        }
    }
    h2 { +"Lunch Team" }

    div {
        id = "team-list-container"  // 변경된 부분
        attributes["hx-get"] = "/team"
        attributes["hx-trigger"] = "load,sse:teamUpdated"
        p(classes = "htmx-indicator") { +"Loading..." }
    }
}

fun BODY.teamList(teams: List<Team>) {
    div {
        id = "team-list-container"
        attributes["hx-get"] = "/team"
        attributes["hx-trigger"] = "sse:teamUpdated"

        teams.forEach { team ->
            div("team-card") {
                h3 { +"팀 ${team.first().name}" }
                ul {
                    team.forEach { participant ->
                        li { +participant.name }
                    }
                }
            }
        }
    }
}

fun BODY.memberList(members: List<Member>, uid: String) {
    ul(classes = "member-list") {
        members.forEach { member ->
            li(classes = "member-item") {
                member(member, uid)
            }
        }
    }
}

fun LI.member(member: Member, uid: String) {
    val (buttonClass, buttonText) = if (member.participate) {
        "join-button" to "참석"
    } else {
        "not-join-button" to "불참"
    }

    div(classes = "member-info") {
        h2(classes = "member-name") { +member.name }

        button(classes = "pl $buttonClass") {
            attributes["hx-put"] = "/members/${member.name}?uid=$uid"
            attributes["hx-target"] = "closest li"
            attributes["hx-swap"] = "outerHTML"

            +buttonText
        }
    }
}

fun BODY.li(cssClass: String? = null, e: LI.() -> Unit) = LI(
    initialAttributes = cssClass?.let { mutableMapOf("class" to it) } ?: mutableMapOf(),
    consumer = consumer
).visit(e)
