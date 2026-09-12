package com.workflex.workation.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** End to end over the real context: the CSV is imported at startup and read back through HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
class WorkationApiIntegrationTest {

	@Autowired private MockMvc mockMvc;

	@Test
	void returnsEveryRowOfTheImportedFile() throws Exception {
		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(5));
	}

	@Test
	void exposesTheBusinessIdentifierAndNeverTheDatabaseKey() throws Exception {
		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(jsonPath("$[0].id").value("w1"))
				.andExpect(jsonPath("$[0].workationId").doesNotExist());
	}

	@Test
	void nestsEachCountryWithItsIsoCode() throws Exception {
		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(jsonPath("$[0].origin.name").value("Germany"))
				.andExpect(jsonPath("$[0].origin.isoCode").value("DE"))
				.andExpect(jsonPath("$[0].destination.isoCode").value("US"));
	}

	@Test
	void serialisesDatesAsIsoStrings() throws Exception {
		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(jsonPath("$[0].start").value("2024-01-02"))
				.andExpect(jsonPath("$[0].end").value("2024-12-31"))
				.andExpect(jsonPath("$[0].workingDays").value(65));
	}

	@Test
	void translatesTheFileRiskTokensIntoDomainNames() throws Exception {
		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(jsonPath("$[0].risk").value("HIGH_RISK"))
				.andExpect(jsonPath("$[3].risk").value("LOW_RISK"))
				.andExpect(jsonPath("$[4].risk").value("NO_RISK"));
	}

	@Test
	void letsTheAngularDevServerReadTheResponse() throws Exception {
		mockMvc
				.perform(get("/workflex/workation").header("Origin", "http://localhost:4200"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
	}

	@Test
	void reportsAnUnknownPathAsAProblemDetail() throws Exception {
		mockMvc
				.perform(get("/workflex/nope"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith("application/problem+json"));
	}
}
