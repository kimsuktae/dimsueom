package example.com.plugins.team

import example.com.plugins.member.Member
import java.time.LocalDate

typealias Team = List<Participant>

fun lunchTeam(init: LuchTeamDsl.() -> Unit) : LuchTeamDsl {
    return LuchTeamDsl()
        .apply(init)
}

class LuchTeamDsl {
    private var participants = listOf<Participant>()
    private var date: LocalDate = LocalDate.now()
    private var lunchTeamHistories: List<List<List<String>>> = mutableListOf()
    private var partySize: Int = 4
    private var lunchTeamDrawStrategy: LunchTeamDrawStrategy = LunchTeamDrawStrategy.KangGunWoo

    init {
        require(partySize >= 2) { "Party Size must be at least 2!" }
    }

    fun participants(block: ParticipantsSelectionDsl.() -> Unit) {
        this.participants = ParticipantsSelectionDsl().apply(block).build()
    }

    fun date(date: LocalDate) {
        this.date = date
    }

    fun histories(lunchTeamHistories: List<List<List<String>>>) {
        this.lunchTeamHistories = lunchTeamHistories
    }

    fun partySize(partySize: Int) {
        this.partySize = partySize
    }

    fun draw(block: DrawStrategyDsl.() -> Unit) {
        this.lunchTeamDrawStrategy = DrawStrategyDsl().apply(block).build()
    }

    fun build(): LunchTeam {
        return lunchTeamDrawStrategy.draw(
            participants = participants,
            randomLunchPartyListHistory = lunchTeamHistories,
            date = date,
            partySize = partySize,
        )
    }
}

class ParticipantsSelectionDsl {
    private var participants: List<Participant> = mutableListOf()

    fun selectFrom(members: List<Member>) {
        this.participants = members.filter { it.participate }.map { it.toParticipate() }
    }

    fun build(): List<Participant> {
        return participants
    }
}


class DrawStrategyDsl {
    private var strategy: LunchTeamDrawStrategy = LunchTeamDrawStrategy.KangGunWoo

    fun strategy(strategy: LunchTeamDrawStrategy) {
        this.strategy = strategy
    }

    fun build(): LunchTeamDrawStrategy {
        return this.strategy
    }
}

fun Member.toParticipate(): Participant {
    return Participant(this.name)
}
