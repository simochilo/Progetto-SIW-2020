package com.example.demo.authentication;

import javax.sql.DataSource;
import static com.example.demo.model.Credentials.ADMIN_ROLE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource datasource;
	
	/**
	 * The method configure defines which URL path should be secured
	 * and which should not (e.g. "/","home" shouldn't be secured)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/", "/index", "/login", "/users/register").permitAll()	// these paths can be accessed by anyone
				.antMatchers(HttpMethod.POST, "/login", "/users/register").permitAll()
				.antMatchers(HttpMethod.GET, "admin/**").hasAnyAuthority(ADMIN_ROLE) 		// the path admin/** is accessible only by an user with ADMIN_ROLE
				.antMatchers(HttpMethod.POST, "admin/**").hasAnyAuthority(ADMIN_ROLE) 
				.anyRequest().authenticated()		// URLs are allowed by any authenticated user
				.and().formLogin()
				.defaultSuccessUrl("/home")
				.and().logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/index")
				.invalidateHttpSession(true)
				.clearAuthentication(true).permitAll();		// operations executed after the logout
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth	
				.jdbcAuthentication()		// allows customization of the JDBC authentication
				.dataSource(this.datasource)
				.authoritiesByUsernameQuery("select username, role from credentials where username=?")
				.usersByUsernameQuery("select username, password, 1 as enabled from credentials where username=?");
				
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
