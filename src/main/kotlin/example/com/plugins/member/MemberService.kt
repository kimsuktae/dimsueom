package example.com.plugins.member

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberService {
    private val members = listOf(
        Member(
            name = "피카츄", participate = true
        ),
        Member(
            name = "라이츄", participate = true
        ),
        Member(
            name = "파이리", participate = false
        ),
        Member(
            name = "꼬부기", participate = false
        ),
        Member(
            name = "버터플", participate = false
        ),
        Member(
            name = "야도란", participate = false
        ),
    )

    suspend fun getAllMembers(): List<Member> = withContext(Dispatchers.IO) {
        members
    }

    suspend fun updateMember(name: String) = withContext(Dispatchers.IO) {
        val member = members.find { it.name == name } ?: throw NotFoundException()
        member.updateParticipate()
        member
    }
}
