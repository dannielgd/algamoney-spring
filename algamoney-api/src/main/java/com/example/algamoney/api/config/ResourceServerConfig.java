package com.example.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true) //Habilita a segurança nos métodos
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
//	@Autowired
//	private UserDetailsService userDetailsService;
	
//	@Autowired
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//			.withUser("admin").password("admin").roles("ROLE"); //usuário e senha fixos
//		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//		
//	}
	
	

	/**
	 * Método que configura a Autorização
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			//.antMatchers("/categorias").permitAll() //para categorias, qualquer um acessa
			.anyRequest().authenticated() //Precisa estar autenticado para qualquer requisição
			
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //desabilitando  a criação de sessão. Sem estado!
			.and()
			.csrf().disable(); //desabilita o suporte a cross-site request forgery.
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}
	
	/**
	 * Para ler a senha encriptada, precisa-se do PasswordEncoder.
	 * @return o objeto para ler a senha encriptada
	 */
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
	
}
