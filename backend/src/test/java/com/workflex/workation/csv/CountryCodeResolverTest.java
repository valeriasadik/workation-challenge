package com.workflex.workation.csv;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CountryCodeResolverTest {

	private final CountryCodeResolver resolver = new CountryCodeResolver();

	@ParameterizedTest
	@CsvSource({
		"Germany,DE",
		"United States,US",
		"Ukraine,UA",
		"Belgium,BE",
		"Spain,ES",
		"Greece,GR",
		"India,IN"
	})
	void resolvesEveryCountryPresentInTheFile(String name, String expectedIso) {
		assertThat(resolver.resolve(name)).contains(expectedIso);
	}

	@Test
	void ignoresCaseAndSurroundingWhitespace() {
		assertThat(resolver.resolve("  gERMANY ")).contains("DE");
	}

	@Test
	void returnsEmptyForAnUnknownOrMissingName() {
		assertThat(resolver.resolve("Atlantis")).isEmpty();
		assertThat(resolver.resolve(" ")).isEmpty();
		assertThat(resolver.resolve(null)).isEmpty();
	}
}
