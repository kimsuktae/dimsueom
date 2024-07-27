package example.com.plugins.sse

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.writeStringUtf8
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

private val sseFlow = MutableSharedFlow<SseEvent>()

suspend fun triggerSSE(author: String) {
    sseFlow.emit(SseEvent(event = "memberUpdated", data = author))
    sseFlow.emit(SseEvent(event = "teamUpdated", data = UUID.randomUUID().toString()))
}

fun Application.configureSse() {
    routing {
        get("/sse") {
            val id = call.request.queryParameters["uid"] ?: throw BadRequestException("no id")
            call.respondSse(sseFlow, id)
        }
    }
}

suspend fun ApplicationCall.respondSse(eventFlow: Flow<SseEvent>, id: String) {
    response.cacheControl(CacheControl.NoCache(null))
    respondBytesWriter(contentType = ContentType.Text.EventStream) {
        writeStringUtf8("data: Connected to SSE\n\n")
        flush()

        eventFlow.collect { event ->
            if (event.data == id)
                return@collect

            if (event.id != null) {
                writeStringUtf8("id: ${event.id}\n")
            }

            if (event.event != null) {
                writeStringUtf8("event: ${event.event}\n")
            }

            for (dataLine in event.data.lines()) {
                writeStringUtf8("data: $dataLine\n")
            }

            writeStringUtf8("\n")
            flush()
        }
    }
}
