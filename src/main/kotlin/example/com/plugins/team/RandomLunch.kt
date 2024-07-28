package example.com.plugins.team

import java.time.LocalDate
import kotlin.math.ceil
import kotlin.random.Random

fun main() {
    val memberList = listOf(
        "정승현",
        "서석교",
        "전세환",
        "김영진",
        "박민수",
        "김석태",
        "조진영",
        "정시원",
        "김명훈",
        "박원석",
        "최호양",
        "남동민",
        "장군우",
    )
    val randomLunchPartyListHistory: MutableList<List<List<String>>> = mutableListOf()
    repeat(104) {
        RandomLunch
            .getRandomLunchPartyList(
                memberList.shuffled().subList(0, Random.nextInt(6, memberList.size - 2)),
                randomLunchPartyListHistory,
                LocalDate.now().plusDays(it.toLong())
            )
            .also { randomLunchPartyListHistory.add(it) }
    }
    randomLunchPartyListHistory.forEachIndexed { index, partyList ->
        println("--- history: $index ---")
        partyList.forEach { party -> println(party) }
    }
    println("--- member to member count")
    val memberToMemberCountMap = RandomLunch.getMemberToMemberCountMap(randomLunchPartyListHistory)
    memberToMemberCountMap.toSortedMap().forEach { (member, memberCountMap) ->
        println("$member -> ${memberCountMap.values.sum()} -> ${memberCountMap.minBy { it.value }} -> ${memberCountMap.maxBy { it.value }} -> $memberCountMap")
    }
}

object RandomLunch {
    fun getRandomLunchPartyList(
        memberList: List<String>,
        randomLunchPartyListHistory: List<List<List<String>>>,
        date: LocalDate = LocalDate.now(),
        partySize: Int = 4,
    ): List<MutableList<String>> {
        val sortedMemberList = memberList.sorted().toMutableList()
        val random = Random(date.toEpochDay() + sortedMemberList.sumOf { it.hashCode() })
        val memberToMemberCountMap = getMemberToMemberCountMap(randomLunchPartyListHistory)
        val partyCount = ceil(sortedMemberList.size.toDouble() / partySize.toDouble()).toInt()
        val partyRange = 0.rangeUntil(partyCount)
        val partyList = partyRange.map { mutableListOf<String>() }

        while (sortedMemberList.isEmpty()) {
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
        return partyList.onEach { it.sorted() }
    }

    fun getMemberToMemberCountMap(randomLunchPartyListHistory: List<List<List<String>>>): MutableMap<String, MutableMap<String, Long>> {
        val memberToMemberCountMap = mutableMapOf<String, MutableMap<String, Long>>()
        randomLunchPartyListHistory.flatten().forEach { party ->
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
