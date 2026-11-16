package bes.tcc.nosecureapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bes.tcc.nosecureapi.dto.MyObject;
import bes.tcc.nosecureapi.repository.BaseObjectRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NoSecureApiApplicationTests {
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
	void getsObjectWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/object/42"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(42))
				.andExpect(jsonPath("$.name").value("Test object"));
	}

	@Test
	void returnsNotFoundWhenObjectDoesNotExist() throws Exception {
		mockMvc.perform(get("/object/99"))
				.andExpect(status().isNotFound());
	}
}
