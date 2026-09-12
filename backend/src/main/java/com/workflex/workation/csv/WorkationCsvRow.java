package com.workflex.workation.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * One row of the CSV, as the file shapes it. Deliberately not the entity: the file's vocabulary is
 * not the domain's, so a change in the source format stops here.
 *
 * <p>Mutable because OpenCSV populates it by reflection.
 */
@Getter
@Setter
public class WorkationCsvRow {

	@CsvBindByName(required = true)
	private String workationId;

	@CsvBindByName(required = true)
	private String employee;

	@CsvBindByName(required = true)
	private String origin;

	@CsvBindByName(required = true)
	private String destination;

	@CsvDate("yyyy-MM-dd")
	@CsvBindByName(required = true)
	private LocalDate start;

	@CsvDate("yyyy-MM-dd")
	@CsvBindByName(required = true)
	private LocalDate end;

	@CsvBindByName(required = true)
	private int workingDays;

	/** Kept as text: translating the file's token into {@code RiskLevel} is the parser's job. */
	@CsvBindByName(required = true)
	private String risk;
}
