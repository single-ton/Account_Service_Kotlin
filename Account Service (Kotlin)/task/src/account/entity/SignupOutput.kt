package account.entity

data class SignupOutput (
    val id: Long,
    val name:String,
    val lastname:String,
    val email:String,
    val roles:Array<String>,
)
