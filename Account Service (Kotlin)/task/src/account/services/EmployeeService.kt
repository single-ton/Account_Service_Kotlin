package account.services

import account.entity.Payment
import account.entity.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate

@Service
class EmployeeService(val db: JdbcTemplate, val transactionTemplate: TransactionTemplate) {
    fun findUsers():List<User> = db.query("select * from users"){ response, _ ->
        User(
            response.getLong("id"),
            response.getString("name"),
            response.getString("lastname"),
            response.getString("email"))
    }
    fun save(user: User){
        db.update("insert into users (name, lastname, email, password) values ( ?, ?, ?, ? )",
             user.firstName, user.lastName, user.email, user.passWord)
    }
    fun getUser(email:String):User?{
        val users = db.query(String.format("select * from users where LOWER(email)=LOWER('%s')", email)){ response, _ ->
            account.entity.User(
                response.getLong("id"),
                response.getString("name"),
                response.getString("lastname"),
                response.getString("email"),
                response.getString("password")
            )
        }
        return if(users.size>0)
            users[0]
        else
            null
    }
    fun changePass(user:User, password:String){
        db.update(String.format("update users set password='%s' where id=%d", password, user.id))
    }
    fun addPayments(payments:List<Payment>){
        transactionTemplate.execute {
            for(p in payments) {
                db.update(String.format("insert into payments values('%s','%s','%s')", p.employee, p.period, p.salary))
            }
        }
    }


}