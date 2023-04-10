package account.controllers

import account.entity.*
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Array
import java.util.*


@RestController
@RequestMapping("api/auth")
class AuthController {
    @Autowired
    lateinit var employeeService: EmployeeService
    var breachedPasswods = arrayOf(
        "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
        "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
        "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    )

    @PostMapping("/signup")
    fun Signup(@RequestBody signupData: SignupData): SignupOutput {
        val users = employeeService.findUsers()

        if(users.find{ user -> user.email.lowercase()==signupData.email.lowercase()}!=null){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!")
        }
        if(signupData.email.contains("@acme.com") &&
            signupData.name != "" &&
            signupData.lastname != ""&&
            signupData.password != ""&&

            !breachedPasswods.contains(signupData.password)&&
            signupData.password.length>=12
        ) {
            val responseData = User(0, signupData.name, signupData.lastname, signupData.email, signupData.password)
            employeeService.save(responseData)
            val user = employeeService.getUser(responseData.email)
            if(users.size==0)
                employeeService.addRole(user!!, "ROLE_ADMINISTRATOR")
            else
                employeeService.addRole(user!!, "ROLE_USER")
            val roles = employeeService.getRoles(user)

            return SignupOutput(user!!.id, responseData.firstName, responseData.lastName,responseData.email,
                roles.toTypedArray()
            )
        }
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!")
    }
    @PostMapping("/changepass")
    fun ChangePass(@RequestHeader headers: HttpHeaders,
                   @RequestBody changePassData: ChangePassData?):ChangedPassOutput{
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        val user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")

        if(changePassData!!.new_password.length<12){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!")
        }
        if(changePassData.new_password==loginPass[1]
            )
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!")
        if(breachedPasswods.contains(changePassData.new_password))
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!")
        employeeService.changePass(user, changePassData.new_password)
        return ChangedPassOutput(user.email.lowercase())
    }
}