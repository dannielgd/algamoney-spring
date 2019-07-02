package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AutohrizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	/**
	 * Autoriza o cliente a usar o recurso
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("angular") //Nome do cliente
			.secret("@ngul@r0") //senha do cliente
			.scopes("read", "write") //define qual escopo está o cliente. (limitações)
			.authorizedGrantTypes("password") //o fluxo de senha. Usa-se o usuario e senha para fazer o access token
			.accessTokenValiditySeconds(1800); //quantos segundos o token fica ativo
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.tokenStore(tokenStore()) //armazena o token em algum lugar para recuperar depois.
			.accessTokenConverter(accessTokenConverter())
			.authenticationManager(authenticationManager);
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks"); // senha que valida o token
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		//return new InMemoryTokenStore(); //Com JWT não se armazena mais em memória
		return new JwtTokenStore(accessTokenConverter());
	}

}
