package account.entity

data class ChangeRoleInfo (
    val user:String,
    val role:String,
    val operation:String,
)