package com.mybus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = "com.mybus")
@EnableWebSecurity
@EnableWebMvcSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginService loginService;
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		/*auth.inMemoryAuthentication().withUser("tom").password("123").roles("USER");
		auth.inMemoryAuthentication().withUser("bill").password("123").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("james").password("123").roles("SUPERADMIN");*/
        auth.userDetailsService( loginService );
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/protected/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/confidential/**").access("hasRole('ROLE_SUPERADMIN')").and()
		.formLogin().loginPage("/login")
		.failureUrl("/login?error")
		.usernameParameter("username")
		.passwordParameter("password").and()
		.logout().logoutSuccessUrl("/login?logout")
		.and().csrf().disable();

	}

}
