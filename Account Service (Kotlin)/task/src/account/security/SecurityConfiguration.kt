package account.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
open class SecurityConfig(
    /*private val customUserDetailsService: CustomUserDetailsService,
    private val passwordEncoderAndMatcher: PasswordEncoder,*/
) : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/auth/signup/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/auth/changepass/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/empl/payment/**").permitAll()//.authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
    /*override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoderAndMatcher)
    }*/



}

/*
@Component
internal class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
    }
}*/
