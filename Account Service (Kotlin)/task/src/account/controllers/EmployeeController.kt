package account.controllers

import account.entity.PaymentOutput
import account.entity.SignupOutput
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("api/empl")
class EmployeeController {
    @Autowired
    lateinit var employeeService: EmployeeService

    fun monthConverter(month:String):String{
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
    @GetMapping("/payment")
    fun getPayment(@RequestHeader headers: HttpHeaders,
                   @RequestParam( value = "period", required = false) period:String?):Any{
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        val user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")

        var payments = employeeService.getPayments(loginPass[0])
        val paymentsOutput=ArrayList<PaymentOutput>()
        if(period!=null){
            try{
                monthConvert(period.split("-")[0])
                for(p in payments){
                    if(p.period==period) {
                        payments = listOf(p)
                        break
                    }
                }
            }catch (e:Exception){
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Month out of range")
            }
        }
        for(p in payments){
            paymentsOutput.add(
                PaymentOutput(
                    user.firstName,
                    user.lastName,
                    String.format("%s-%s",monthConverter(p.period.split("-")[0]), p.period.split("-")[1]),
                    String.format("%d dollar(s) %d cent(s)",(p.salary/100).toInt(),p.salary%100)
                ))
        }
        if(period!=null)
            return paymentsOutput[0]
        return paymentsOutput
    }

}