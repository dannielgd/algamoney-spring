package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	/**
	 * O OAuth2AccessToken, com o refresh token(devemos evitar retornar para o cliente)
	 * é retornado em outros momentos, como uma consulta ao banco.Para evitar isso, colocamos a linha para
	 * filtrar quando é retornado o OAuth2AccessToken.
	 */
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod().getName().equals("postAccessToken"); //limita o OAuth2AccessToken ser retornado em momentos indevidos.
	}
	
	/**
	 * Recuperamos o corpo da requisição e o valor do refresh token para colocar em um cookie.
	 */
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		//esse objeto ainda é o body, só que precisamos de um método que retire o refreshToken de lá
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}

	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		
		token.setRefreshToken(null);
		
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true); //somente é acessível em http.
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps()); //deve funcionar apenas em https. // Mudar para true em produção.
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token"); //qual o caminho que o cookie vai ser enviado pelo browser
		refreshTokenCookie.setMaxAge(2592000); //30 dias para expirar. (em segundos).
		resp.addCookie(refreshTokenCookie);
	}
	
}
