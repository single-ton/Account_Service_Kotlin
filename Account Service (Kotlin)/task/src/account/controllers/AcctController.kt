package account.controllers

import account.entity.*
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("api/acct")
class AcctController {
    @Autowired
    lateinit var employeeService: EmployeeService
    fun monthConvert(month:String):String{
        if(month=="01")
            return "January"
        if(month=="02")
            return "February"
        if(month=="03")
            return "March"
        if(month.toInt()>12||month.toInt()<1)
            throw Exception("month out of rahge")
        return "other"
    }
    @PostMapping("/payments")
    fun PostPayments(@RequestBody payments: List<Payment>): StatusOutput {
        try {
            for(p in payments){
                monthConvert(p.period.split("-")[0])
            }
            employeeService.addPayments(payments)
        }catch(e:Exception){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some exception")
        }
        return StatusOutput()
    }
    @PutMapping("/payments")
    fun PutPayments(@RequestBody payment: Payment): StatusOutput {
        try {
            monthConvert(payment.period.split("-")[0])
            employeeService.updatePayments(payment)
        }catch(e:Exception){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some exception")
        }
        return StatusOutput("Updated successfully!")
    }

    @GetMapping("/payments")
    fun GetPayments(@RequestHeader headers: HttpHeaders,
                    @RequestParam period:String): Payment {
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        val user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")

        try {
            monthConvert(period)
            val payment = employeeService.getPayment(user.email,period)
            return payment!!
        }catch(e:Exception){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some exception")
        }
    }
}