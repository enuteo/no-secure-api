package bes.tcc.no_roles_api;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bes.tcc.no_roles_api.dto.MyObject;
import bes.tcc.no_roles_api.exception.AccessNotAllowedException;
import bes.tcc.no_roles_api.repository.BaseObjectRepository;
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
class NoRolesApiApplicationTests {
	private static final Map<String, Map<String, Object>> TOKENS = Map.of(
			"without-role", Map.of("realm_access", Map.of("roles", List.of("user"))),
			"outsider", Map.of("realm_access", Map.of("roles", List.of("objectrole"))));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BaseObjectRepository baseObjectRepository;

	@BeforeEach
	void setUpRepository() {
		when(baseObjectRepository.findByIdVisibleToUser(42L, "without-role"))
				.thenReturn(Optional.of(new MyObject(42L, "Test object")));
		when(baseObjectRepository.findByIdVisibleToUser(42L, "outsider"))
				.thenThrow(new AccessNotAllowedException());
	}

	@Test
	void contextLoads() {
	}

	@Test
	void getsObjectWithValidTokenWithoutCheckingRoles() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer without-role"))
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
	void deniesUsersWithoutObjectAccessRegardlessOfRole() throws Exception {
		mockMvc.perform(get("/object/42").header(HttpHeaders.AUTHORIZATION, "Bearer outsider"))
				.andExpect(status().isForbidden());
		verify(baseObjectRepository).findByIdVisibleToUser(42L, "outsider");
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
