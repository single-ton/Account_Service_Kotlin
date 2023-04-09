package account.controllers

import account.entity.*
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/acct")
class AcctController {
    @Autowired
    lateinit var employeeService: EmployeeService
    @PostMapping("/payments")
    fun PostPayments(@RequestBody payments: List<Payment>): StatusOutput {
        try {
            employeeService.addPayments(payments)
        }catch(e:Exception){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some exception")
        }
        return StatusOutput()
    }
}