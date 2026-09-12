package com.workflex.workation.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.workflex.workation.domain.RiskLevel;
import com.workflex.workation.service.CountryDto;
import com.workflex.workation.service.WorkationDto;
import com.workflex.workation.service.WorkationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** The web contract in isolation: no database, no CSV import. Only route, status and JSON shape. */
@WebMvcTest(WorkationController.class)
@EnableConfigurationProperties(CorsProperties.class)
class WorkationControllerTest {

	@Autowired private MockMvc mockMvc;

	@MockitoBean private WorkationService workationService;

	@Test
	void servesTheEndpointAtTheExactPathTheBriefAsksFor() throws Exception {
		given(workationService.findAll()).willReturn(List.of(sample()));

		mockMvc
				.perform(get("/workflex/workation"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("w1"))
				.andExpect(jsonPath("$[0].origin.isoCode").value("DE"))
				.andExpect(jsonPath("$[0].risk").value("HIGH_RISK"));
	}

	@Test
	void returnsAnEmptyArrayWhenThereIsNothingToShow() throws Exception {
		given(workationService.findAll()).willReturn(List.of());

		mockMvc.perform(get("/workflex/workation")).andExpect(content().json("[]"));
	}

	private static WorkationDto sample() {
		return new WorkationDto(
				"w1",
				"Steffen Jacobs",
				new CountryDto("Germany", "DE"),
				new CountryDto("Spain", "ES"),
				LocalDate.of(2024, 1, 2),
				LocalDate.of(2024, 1, 3),
				1,
				RiskLevel.HIGH_RISK);
	}
}
