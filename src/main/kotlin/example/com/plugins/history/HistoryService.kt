package example.com.plugins.history

class HistoryService {
    suspend fun getAllHistories(): List<LunchTeamHistory> {
        return emptyList()
    }
}

typealias LunchTeamHistory = List<List<String>>
