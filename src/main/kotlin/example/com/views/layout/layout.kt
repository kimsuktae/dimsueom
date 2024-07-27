package example.com.views.layout

import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.script

fun HTML.layout(e: BODY.() -> Unit) {
    head {
        link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")

        script(src = "https://unpkg.com/htmx.org@1.9.10") {}
        script(src = "https://unpkg.com/htmx.org@1.9.10/dist/ext/json-enc.js") {}
        script(src = "https://unpkg.com/htmx.org@1.9.10/dist/ext/sse.js") {}
        script(src = "https://unpkg.com/htmx.org/dist/ext/debug.js") {}
    }

    body {
        e()
    }
}
