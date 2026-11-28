package bes.tcc.no_validation_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bes.tcc.no_validation_api.dto.MyObject;
import bes.tcc.no_validation_api.repository.BaseObjectRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NoValidationApiApplicationTests {
	private static final Map<String, Map<String, Object>> TOKENS = Map.of(
			"object-role-outsider", Map.of("realm_access", Map.of("roles", List.of("objectrole"))),
			"no-role", Map.of("realm_access", Map.of("roles", List.of("user"))));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BaseObjectRepository baseObjectRepository;

	@BeforeEach
	void setUpRepository() {
		when(baseObjectRepository.findById(42L)).thenReturn(Optional.of(new MyObject(42L, "Test object")));
	}

	@Test
	void contextLoads() {
	}

	@Test
	void validObjectRoleCanReadAnyObjectRegardlessOfOwnership() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer object-role-outsider"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(42))
				.andExpect(jsonPath("$.name").value("Test object"));
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/object/42"))
				.andExpect(status().is(407))
				.andExpect(content().string("não há autenticação"));
	}

	@Test
	void requiresAValidToken() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer invalid"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsAuthenticatedUserWithoutObjectRole() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer no-role"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("não autorizado a funcionalidade"));
	}

	@TestConfiguration
	static class TestJwtDecoderConfig {
		@Bean
		JwtDecoder jwtDecoder() {
			return token -> {
				Map<String, Object> claims = TOKENS.get(token);
				if (claims == null) {
					throw new BadJwtException("Unknown test token");
				}
				return Jwt.withTokenValue(token)
						.header("alg", "RS256")
						.subject(token)
						.claim("preferred_username", token)
						.claims(existing -> existing.putAll(claims))
						.issuedAt(Instant.now())
						.expiresAt(Instant.now().plusSeconds(300))
						.build();
			};
		}
	}
}
