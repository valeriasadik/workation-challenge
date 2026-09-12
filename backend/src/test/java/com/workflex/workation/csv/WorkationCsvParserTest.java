package com.workflex.workation.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.workflex.workation.domain.RiskLevel;
import com.workflex.workation.domain.Workation;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkationCsvParserTest {

	private static final String HEADER =
			"workationId,employee,origin,destination,start,end,workingDays,risk\n";

	private final WorkationCsvParser parser = new WorkationCsvParser(new CountryCodeResolver());

	@Test
	void mapsEveryColumnAndEnrichesTheIsoCodes() {
		Result result =
				parse(HEADER + "w1,Steffen Jacobs,Germany,United States,2024-01-02,2024-12-31,65,HIGH\n");

		assertThat(result.skipped()).isZero();
		Workation w = result.rows().getFirst();
		assertThat(w.getWorkationId()).isEqualTo("w1");
		assertThat(w.getEmployee()).isEqualTo("Steffen Jacobs");
		assertThat(w.getOriginIsoCode()).isEqualTo("DE");
		assertThat(w.getDestinationIsoCode()).isEqualTo("US");
		assertThat(w.getStart()).isEqualTo(LocalDate.of(2024, 1, 2));
		assertThat(w.getEnd()).isEqualTo(LocalDate.of(2024, 12, 31));
		assertThat(w.getWorkingDays()).isEqualTo(65);
		assertThat(w.getRisk()).isEqualTo(RiskLevel.HIGH_RISK);
	}

	/** A comma inside a quoted field: anything built on split(",") fails here. */
	@Test
	void readsAFieldThatContainsAQuotedComma() {
		Result result =
				parse(HEADER + "w1,\"Jacobs, Steffen\",Germany,Spain,2024-01-02,2024-01-03,1,HIGH\n");

		assertThat(result.rows().getFirst().getEmployee()).isEqualTo("Jacobs, Steffen");
	}

	@Test
	void translatesEveryRiskToken() {
		Result result =
				parse(
						HEADER
								+ "w1,A,Germany,Spain,2024-01-02,2024-01-03,1,HIGH\n"
								+ "w2,B,Germany,Spain,2024-01-02,2024-01-03,1,LOW\n"
								+ "w3,C,Germany,Spain,2024-01-02,2024-01-03,1,NO\n");

		assertThat(result.rows())
				.extracting(Workation::getRisk)
				.containsExactly(RiskLevel.HIGH_RISK, RiskLevel.LOW_RISK, RiskLevel.NO_RISK);
	}

	@Test
	void acceptsAFileWithNoDataRows() {
		assertThat(parse(HEADER).rows()).isEmpty();
	}

	/** One unusable row must not cost us the others. */
	@Test
	void keepsTheGoodRowsWhenOneIsBad() {
		Result result =
				parse(
						HEADER
								+ "w1,A,Germany,Spain,2024-01-02,2024-01-03,1,HIGH\n"
								+ "w2,B,Germany,Atlantis,2024-01-02,2024-01-03,1,HIGH\n"
								+ "w3,C,Germany,Greece,2024-01-02,2024-01-03,1,LOW\n");

		assertThat(result.rows()).extracting(Workation::getWorkationId).containsExactly("w1", "w3");
		assertThat(result.skipped()).isEqualTo(1);
	}

	@Test
	void skipsRowsThatCannotBeRead() {
		assertThat(parse(HEADER + "w1,,Germany,Spain,2024-01-02,2024-01-03,1,HIGH\n").skipped())
				.isEqualTo(1);
		assertThat(parse(HEADER + "w1,A,Germany,Spain,02/01/2024,2024-01-03,1,HIGH\n").skipped())
				.isEqualTo(1);
		assertThat(parse(HEADER + "w1,A,Germany,Spain,2024-01-02,2024-01-03,many,HIGH\n").skipped())
				.isEqualTo(1);
		assertThat(parse(HEADER + "w1,A,Germany,Spain,2024-01-02,2024-01-03,1,MEDIUM\n").skipped())
				.isEqualTo(1);
	}

	private record Result(List<Workation> rows, int skipped) {}

	private Result parse(String csv) {
		List<Workation> rows = new ArrayList<>();
		int skipped = parser.parse(new StringReader(csv), rows::add);
		return new Result(rows, skipped);
	}
}
