package com.mx.chedraui.vtexPromo.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mx.chedraui.vtexPromo.entity.sql.ConfigurationProperties;
import com.mx.chedraui.vtexPromo.repository.sql.ConfigurationPropertiesRepository;
import com.mx.chedraui.vtexPromo.utils.DateUtil;

/**
 * Client credentials de Entra ID para SMTP XOAUTH2.
 * Scope por defecto: https://outlook.office365.com/.default (permiso SMTP.SendAsApp).
 */
@Service
public class OAuthTokenService {

	private static final String DEFAULT_SCOPE = "https://outlook.office365.com/.default";

	@Autowired
	ConfigurationPropertiesRepository configurationPropertiesRepository;

	private final RestTemplate restTemplate = restTemplateWithoutErrorHandler();

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String accessToken() throws MessagingException {
		String tenantId = required("mail.azure.tenant.id");
		String clientId = required("mail.azure.client.id");
		String clientSecret = required("mail.azure.client.secret");
		String scope = config("mail.azure.oauth.scope");
		if (scope.isEmpty()) {
			scope = DEFAULT_SCOPE;
		}

		String url = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "oauth=client_credentials scope=" + scope);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("client_id", clientId);
		form.add("client_secret", clientSecret);
		form.add("scope", scope);
		form.add("grant_type", "client_credentials");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, String>>(form, headers), String.class);
		int status = response.getStatusCodeValue();
		String raw = response.getBody() == null ? "" : response.getBody();
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "oauth http=" + status);
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "oauth body=" + raw);
		if (status < 200 || status >= 300) {
			throw new MessagingException("OAuthTokenError HTTP " + status + " " + raw);
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> body = objectMapper.readValue(raw, Map.class);
			if (body.get("access_token") == null) {
				throw new MessagingException("OAuthTokenError HTTP " + status + " sin access_token " + raw);
			}
			String token = String.valueOf(body.get("access_token"));
			System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "oauth=OK expires_in="
					+ body.get("expires_in") + "s token_len=" + token.length());
			return token;
		} catch (MessagingException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new MessagingException("OAuthTokenError HTTP " + status + " body=" + raw + " parse=" + ex, ex);
		}
	}

	private static RestTemplate restTemplateWithoutErrorHandler() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setOutputStreaming(false);
		RestTemplate template = new RestTemplate(requestFactory);
		template.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) {
				return false;
			}
		});
		return template;
	}

	private String required(String key) throws MessagingException {
		String value = config(key);
		if (value.isEmpty()) {
			throw new MessagingException("Falta " + key + " en dbo.CONFIGURATIONS");
		}
		return value;
	}

	private String config(String key) {
		List<ConfigurationProperties> items = configurationPropertiesRepository.getByName(key);
		if (items == null || items.isEmpty() || items.get(0).getDescription() == null) {
			return "";
		}
		return items.get(0).getDescription().trim();
	}

}
