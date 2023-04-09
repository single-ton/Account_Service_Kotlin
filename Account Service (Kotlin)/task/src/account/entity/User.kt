package account.entity

import java.util.*
import javax.persistence.*


open class User(var id: Long=0,
                open var firstName: String = "",
                open var lastName: String = "",
                open var email: String = "",
                open var passWord: String = "") {


}