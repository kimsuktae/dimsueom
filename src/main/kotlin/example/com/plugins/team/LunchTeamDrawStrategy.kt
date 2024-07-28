package example.com.plugins.team

import example.com.plugins.history.LunchTeamHistory
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.random.Random

abstract class LunchTeamDrawStrategy {
    abstract fun draw(
        participants: List<Participant>,
        randomLunchPartyListHistory: List<LunchTeamHistory>? = null,
        date: LocalDate = LocalDate.now(),
        partySize: Int = 4,
    ): LunchTeam

    class KangGunWoo : LunchTeamDrawStrategy() {
        override fun draw(
            participants: List<Participant>,
            randomLunchPartyListHistory: List<LunchTeamHistory>?,
            date: LocalDate,
            partySize: Int
        ): LunchTeam {
            val teams = participants
                .shuffled()
                .chunked((participants.size + partySize - 1) / partySize)
                .map { it.toList() }

            return LunchTeam(
                teams = teams,
                date = date,
            )
        }
    }

    class HistoryAwareRandomLunchTeamDrawStrategy : LunchTeamDrawStrategy() {
        override fun draw(
            participants: List<Participant>,
            randomLunchPartyListHistory: List<LunchTeamHistory>?,
            date: LocalDate,
            partySize: Int
        ): LunchTeam {
            val sortedMemberList = participants.map { it.name }.sorted().toMutableList()
            val random = Random(date.toEpochDay() + sortedMemberList.sumOf { it.hashCode() })
            val memberToMemberCountMap = randomLunchPartyListHistory?.getMemberToMemberCountMap() ?: emptyMap()
            val partyCount = ceil(sortedMemberList.size.toDouble() / partySize.toDouble()).toInt()
            val partyRange = 0.rangeUntil(partyCount)
            val partyList = partyRange.map { mutableListOf<String>() }

            while (sortedMemberList.isNotEmpty()) {
                partyRange.forEach { partyIndex ->
                    if (sortedMemberList.isEmpty()) {
                        return@forEach
                    }
                    val party = partyList[partyIndex]
                    val randomMember = if (party.isEmpty()) {
                        sortedMemberList.removeAt(random.nextInt(sortedMemberList.size))
                    } else {
                        val memberScoreMap = sortedMemberList.associateWith { member ->
                            party
                                .minOf { partyMember ->
                                    memberToMemberCountMap
                                        .getOrDefault(member, mutableMapOf())
                                        .getOrDefault(partyMember, 0)
                                        .let {
                                            if (it == 0L) {
                                                0
                                            } else {
                                                random.nextLong(it)
                                            }
                                        }
                                }
                        }
                        val minScore = memberScoreMap.minOf { it.value }
                        val midScoreMemberList = memberScoreMap
                            .filterValues { it == minScore }
                            .keys
                        midScoreMemberList
                            .sorted()[random.nextInt(midScoreMemberList.size)]
                            .also {
                                sortedMemberList.remove(it)
                            }
                    }
                    party.add(randomMember)
                }
            }

            val teams = partyList
                .map { it.sorted().map { name -> Participant(name) } }

            return LunchTeam(
                teams = teams,
                date = date,
            )
        }

        private fun List<LunchTeamHistory>.getMemberToMemberCountMap(): MutableMap<String, MutableMap<String, Long>> {
            val memberToMemberCountMap = mutableMapOf<String, MutableMap<String, Long>>()
            this.flatten().forEach { party ->
                party.forEach { member ->
                    party.forEach {
                        if (it != member) {
                            val memberCountMap = memberToMemberCountMap.getOrPut(member) { mutableMapOf() }
                            memberCountMap.merge(it, 1, Long::plus)
                        }
                    }
                }
            }
            return memberToMemberCountMap
        }
    }

    companion object {
        val KangGunWoo = KangGunWoo()
        val HistoryAwareRandomDrawStrategy = HistoryAwareRandomLunchTeamDrawStrategy()
    }
}
