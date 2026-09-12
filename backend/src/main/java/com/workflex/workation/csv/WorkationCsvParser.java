package com.workflex.workation.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.workflex.workation.domain.RiskLevel;
import com.workflex.workation.domain.Workation;
import java.io.Reader;
import java.util.Locale;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Turns the CSV into {@link Workation} instances, one row at a time.
 *
 * <p>Rows go to a {@link Consumer} instead of being collected into a list, and the {@link CsvToBean}
 * is iterated rather than {@code parse()}d, so memory does not grow with the size of the file.
 *
 * <p>A bad row is skipped and logged; only an unreadable file fails.
 */
@Component
public class WorkationCsvParser {

	private static final Logger log = LoggerFactory.getLogger(WorkationCsvParser.class);

	private final CountryCodeResolver countryCodeResolver;

	public WorkationCsvParser(CountryCodeResolver countryCodeResolver) {
		this.countryCodeResolver = countryCodeResolver;
	}

	/**
	 * @return how many rows were skipped, so the caller can report it
	 * @throws CsvImportException if the file itself cannot be read
	 */
	public int parse(Reader reader, Consumer<Workation> onWorkation) {
		CsvToBean<WorkationCsvRow> rows =
				new CsvToBeanBuilder<WorkationCsvRow>(reader)
						.withType(WorkationCsvRow.class)
						.withIgnoreLeadingWhiteSpace(true)
						.withThrowExceptions(false)
						.build();

		int skipped = 0;
		try {
			for (WorkationCsvRow row : rows) {
				try {
					onWorkation.accept(toWorkation(row));
				} catch (CsvImportException e) {
					log.warn("Skipped workation '{}': {}", row.getWorkationId(), e.getMessage());
					skipped++;
				}
			}
		} catch (RuntimeException e) {
			throw new CsvImportException("Could not read the workations CSV: " + e.getMessage(), e);
		}

		// Rows OpenCSV itself rejected: missing column, unparseable date, non-numeric working days.
		for (CsvException e : rows.getCapturedExceptions()) {
			log.warn("Skipped line {}: {}", e.getLineNumber(), e.getMessage());
			skipped++;
		}
		return skipped;
	}

	private Workation toWorkation(WorkationCsvRow row) {
		String origin = row.getOrigin().trim();
		String destination = row.getDestination().trim();
		return Workation.builder()
				.workationId(row.getWorkationId().trim())
				.employee(row.getEmployee().trim())
				.originCountry(origin)
				.originIsoCode(isoCode(origin))
				.destinationCountry(destination)
				.destinationIsoCode(isoCode(destination))
				.start(row.getStart())
				.end(row.getEnd())
				.workingDays(row.getWorkingDays())
				.risk(risk(row.getRisk()))
				.build();
	}

	/** Resolved once per row at import time, so the stored row is complete. */
	private String isoCode(String countryName) {
		return countryCodeResolver
				.resolve(countryName)
				.orElseThrow(() -> new CsvImportException("unknown country '" + countryName + "'"));
	}

	/** The file's vocabulary is translated here so {@link RiskLevel} stays unaware of the CSV. */
	private RiskLevel risk(String value) {
		return switch (value.trim().toUpperCase(Locale.ROOT)) {
			case "HIGH" -> RiskLevel.HIGH_RISK;
			case "LOW" -> RiskLevel.LOW_RISK;
			case "NO" -> RiskLevel.NO_RISK;
			default ->
					throw new CsvImportException("unknown risk '" + value + "' (expected HIGH, LOW or NO)");
		};
	}
}
