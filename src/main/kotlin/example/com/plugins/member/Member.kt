package example.com.plugins.member

data class Member(
    var name: String,
    var participate: Boolean,
) {
    fun updateParticipate() {
        this.participate = !this.participate
    }
}
