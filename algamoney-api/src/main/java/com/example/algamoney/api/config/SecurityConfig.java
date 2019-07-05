package com.example.algamoney.api.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Usa-se esssa classe quando usamos autenticação do tipo basic. O OAUTH2 usa o ResourceServerConfig
 * @author Danniel
 *
 */
//@Configuration //Não é necessário. A anotação abaixo já tem essa anotação.
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * Método que configura a Autenticação
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("admin").password("{noop}admin").roles("ROLE");
	}
	
	/**
	 * Método que configura a Autorização
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/categorias").permitAll() //para categorias, qualquer um acessa
			.anyRequest().authenticated() //Precisa estar autenticado para qualquer requisição
			.and().httpBasic() //Tipo de autenticação
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //desabilitando  a criação de sessão. Sem estado!
			.and()
			.csrf().disable(); //desabilita o suporte a cross-site request forgery.
	}
	
}
