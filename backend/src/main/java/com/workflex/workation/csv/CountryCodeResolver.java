package com.workflex.workation.csv;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Maps an English country name to its ISO 3166-1 alpha-2 code using the JDK's own locale data, so a
 * new country needs no code change.
 */
@Component
public class CountryCodeResolver {

	private final Map<String, String> isoByName;

	public CountryCodeResolver() {
		Map<String, String> byName = new HashMap<>();
		for (String iso : Locale.getISOCountries()) {
			byName.put(normalise(Locale.of("", iso).getDisplayCountry(Locale.ENGLISH)), iso);
		}
		this.isoByName = Map.copyOf(byName);
	}

	public Optional<String> resolve(String countryName) {
		if (countryName == null || countryName.isBlank()) {
			return Optional.empty();
		}
		return Optional.ofNullable(isoByName.get(normalise(countryName)));
	}

	/** {@code Locale.ROOT} on purpose: a Turkish locale would lowercase "I" to a dotless "ı". */
	private static String normalise(String name) {
		return name.trim().toLowerCase(Locale.ROOT);
	}
}
