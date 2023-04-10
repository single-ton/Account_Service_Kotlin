package account.controllers

import account.entity.ChangePassData
import account.entity.ChangeRoleInfo
import account.entity.SignupOutput
import account.entity.UserStatusOutput
import account.services.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("api/admin/user")
class AminController {

    @Autowired
    lateinit var employeeService: EmployeeService
    @PutMapping("/role")
    fun PutUserRole(@RequestHeader headers: HttpHeaders,
                    @RequestBody changeRoleInfo: ChangeRoleInfo
    ): SignupOutput {
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        var user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val roles = employeeService.getRoles(user)
        if (!roles.contains("ROLE_ADMIN"))
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied!")

        user = employeeService.getUser(changeRoleInfo.user)
        if(user==null)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        if (changeRoleInfo.operation=="GRANT"){
            employeeService.addRole(user,changeRoleInfo.role)
            val roles=employeeService.getRoles(user)
            return SignupOutput(user.id,user.firstName,user.lastName,user.email,roles.toTypedArray())
        }
        else{//remove
            var roles = employeeService.getRoles(user)
            if(!roles.contains(changeRoleInfo.role))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!")
            if(roles.size==1)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!")
            if(changeRoleInfo.role=="ROLE_ADMINISTRATOR")
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!")
            employeeService.deleteRole(user, changeRoleInfo.role)
            roles=employeeService.getRoles(user)
            return SignupOutput(user.id,user.firstName,user.lastName,user.email,roles.toTypedArray())
        }
    }
    @DeleteMapping(path=["/{email}", "/"])
    fun DeleteUser(@RequestHeader headers: HttpHeaders,
                   @PathVariable(name="email", required = false) email:String?
    ):UserStatusOutput{
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        var user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val roles = employeeService.getRoles(user)
        if (!roles.contains("ROLE_ADMIN"))
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied!")

        val delUser = employeeService.getUser(email!!)
        if(delUser==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found!")
        val rolesDelUser = employeeService.getRoles(delUser)
        if(rolesDelUser.contains("ROLE_ADMINISTRATOR"))
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Can't remove ADMINISTRATOR role!")

        employeeService.deleteUser(delUser)
        return UserStatusOutput(delUser.email.lowercase(), "Deleted successfully!")
    }

    @GetMapping("/")
    fun GetRoles(@RequestHeader headers: HttpHeaders
    ):Any?{
        if(headers["authorization"]==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val loginPass = Base64.getDecoder().decode(headers["authorization"]?.get(0)!!.split(" ")[1])
            .toString(charset("utf-8")).split(":")
        var user = employeeService.getUser(loginPass[0])
        if(user==null)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        if(user.passWord!=loginPass[1])
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val roles = employeeService.getRoles(user)
        if (!roles.contains("ROLE_ADMINISTRATOR"))
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied!")
        return null
    }
}