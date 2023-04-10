package account.services

import account.entity.Payment
import account.entity.Role
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
    fun getPayments(email:String):List<Payment>{
        val payments = db.query(String.format("select * from payments where LOWER(employee)=LOWER('%s') order by period DESC", email)) { response, _ ->
            Payment(
                response.getString("employee"),
                response.getString("period"),
                response.getLong("salary")
            )
        }
        return payments
    }
    fun getPayment(email:String, period:String):Payment?{
        val payments = db.query(String.format(
            "select * from payments where LOWER(employee)=LOWER('%s') and period='%s' order by period DESC",
            email, period))
        { response, _ ->
            Payment(
                response.getString("employee"),
                response.getString("period"),
                response.getLong("salary")
            )
        }
        if(payments.size==0)
            return null
        return payments[0]
    }
    fun updatePayments(payment:Payment){
        db.update(String.format("update payments set salary=%d where employee='%s' and period='%s'", payment.salary, payment.employee, payment.period))

    }
    fun addRole(user:User, role:String){
        db.update(String.format("insert into roles(user_id, role) values(%d, '%s')", user.id, role))
    }
    fun deleteRole(user:User, role:String){
        db.update(String.format("delete from roles where id=%d and role='%s'", user.id, role))
    }
    fun getRoles(user:User):List<String>{
        val roles = db.query(String.format("select* from roles where user_id=%d", user.id))
        {
            response, _ -> Role(response.getLong("id"),
                response.getLong("user_id"),
                response.getString("role"))

        }
        val rolesList=ArrayList<String>()
        for(r in roles){
            rolesList.add(r.role)
        }
        return rolesList
    }
    fun deleteUser(user:User){
        db.update(String.format("delete * from roles where user_id=%d", user.id))
        db.update(String.format("delete * from users where id=%d", user.id))
    }


}