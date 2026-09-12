package com.workflex.workation.service;

import com.workflex.workation.domain.RiskLevel;
import java.time.LocalDate;

/**
 * The API contract, deliberately not the entity: the schema can change without breaking the client.
 *
 * <p>{@code id} is the business identifier from the file, never the database key. {@code risk} travels
 * as the enum name; turning it into a label and a colour is the client's job.
 */
public record WorkationDto(
		String id,
		String employee,
		CountryDto origin,
		CountryDto destination,
		LocalDate start,
		LocalDate end,
		int workingDays,
		RiskLevel risk) {}
