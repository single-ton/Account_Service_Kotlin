package account.controllers

import account.entity.SignupOutput
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("api/empl")
class EmployeeController {
    @Autowired
    lateinit var employeeService: EmployeeService

    @GetMapping("/payment")
    fun getPayment(@RequestHeader headers: HttpHeaders): SignupOutput {
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        val user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        return SignupOutput(user.id, user.firstName, user.lastName, user.email)
    }
}