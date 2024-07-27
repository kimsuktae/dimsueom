package example.com.plugins.team

import example.com.plugins.member.Member
import example.com.plugins.member.MemberService

class TeamService(
    private val memberService: MemberService,
) {
    private var team = listOf<Participant>()

    suspend fun getTeam(): List<Participant> {
        team = memberService.getAllMembers()
            .filter { it.participate }
            .map { it.toParticipate() }
            .also { team = it }

        return team
    }
}

private fun Member.toParticipate(): Participant {
    return Participant(this.name)
}


