package com.workflex.workation.csv;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Import settings, bound from {@code app.csv.*}.
 *
 * <p>{@code location} is a {@link Resource}, not a {@code String}: Spring resolves {@code classpath:}
 * the same way from Gradle, from the jar and from a test, so no working-directory guessing is needed.
 */
@ConfigurationProperties(prefix = "app.csv")
public record CsvImportProperties(Resource location, int batchSize) {}
