package example.com.plugins.team

import example.com.plugins.member.Member
import example.com.plugins.member.MemberService
import kotlinx.coroutines.runBlocking

class TeamService(
    private val memberService: MemberService,
) {
    private var team = listOf<Participant>()

    fun getTeam(): List<Participant> {
        runBlocking {
            team = memberService.getAllMembers()
                .filter { it.participate }
                .map { it.toParticipate() }
                .also { team = it }
        }

        return team
    }
}

private fun Member.toParticipate(): Participant {
    return Participant(this.name)
}


