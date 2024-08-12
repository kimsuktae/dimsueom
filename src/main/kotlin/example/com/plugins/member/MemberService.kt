package example.com.plugins.member

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberService {
    private val members = listOf(
        Member(
            name = "정승현", participate = false
        ),
        Member(
            name = "서석교", participate = false
        ),
        Member(
            name = "전세환", participate = false
        ),
        Member(
            name = "김영진", participate = false
        ),
        Member(
            name = "박민수", participate = false
        ),
        Member(
            name = "김석태", participate = false
        ),
        Member(
            name = "조진영", participate = false
        ),
        Member(
            name = "정시원", participate = false
        ),
        Member(
            name = "김명훈", participate = false
        ),
        Member(
            name = "박원석", participate = false
        ),
        Member(
            name = "최호양", participate = false
        ),
        Member(
            name = "남동민", participate = false
        ),
        Member(
            name = "장군우", participate = false
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
