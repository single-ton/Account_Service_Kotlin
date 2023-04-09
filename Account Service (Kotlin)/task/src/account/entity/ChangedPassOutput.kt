package account.entity

data class ChangedPassOutput (
    val email:String,
    val status:String="The password has been updated successfully"
)