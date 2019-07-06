package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

@Configuration
@EnableAuthorizationServer
public class AutohrizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/**
	 * Autoriza o cliente a usar o recurso
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("angular") //Nome do cliente
			.secret("$2a$10$GKIwuojnvZUAJKUBtI8Au..EbTX51BreHtQwppNDsySyVGQ.ln6E.") //senha do cliente - @ngul@r0
			.scopes("read", "write") //define qual escopo está o cliente. (limitações)
			.authorizedGrantTypes("password", "refresh_token") //o fluxo de senha. Usa-se o usuario e senha para fazer o access token
			.accessTokenValiditySeconds(1800) //quantos segundos o token fica ativo
			.refreshTokenValiditySeconds(3600*24)
		.and()
			.withClient("mobile") //Nome do cliente
			.secret("$2a$10$YnDT.82RBA7yt0K7NSEUW.gjVFlnWW38aMFzxXhL1tEFpOADlg7MS") //senha do cliente - @ngul@r0
			.scopes("read") //define qual escopo está o cliente. (limitações)
			.authorizedGrantTypes("password", "refresh_token") //o fluxo de senha. Usa-se o usuario e senha para fazer o access token
			.accessTokenValiditySeconds(1800) //quantos segundos o token fica ativo
			.refreshTokenValiditySeconds(3600*24);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore()) //armazena o token em algum lugar para recuperar depois.
//			.accessTokenConverter(accessTokenConverter())
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false) //enquanto o usuário está usando a aplicação, ele nao se desloga
			.userDetailsService(this.userDetailsService) //Pega o usuário e senha e valida
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
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}

}
