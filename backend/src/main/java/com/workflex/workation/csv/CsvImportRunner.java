package com.workflex.workation.csv;

import com.workflex.workation.domain.Workation;
import com.workflex.workation.domain.WorkationRepository;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports the CSV once the context is ready. The database is in-memory and rebuilt on every start, so
 * a full import keeps file and database in sync with no synchronisation logic.
 */
@Component
@RequiredArgsConstructor
public class CsvImportRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(CsvImportRunner.class);

	private final WorkationCsvParser parser;
	private final WorkationRepository repository;
	private final CsvImportProperties properties;
	private final EntityManager entityManager;

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws IOException {
		List<Workation> batch = new ArrayList<>(properties.batchSize());
		AtomicInteger imported = new AtomicInteger();
		int skipped;

		try (Reader reader = openCsv()) {
			skipped =
					parser.parse(
							reader,
							workation -> {
								batch.add(workation);
								if (batch.size() >= properties.batchSize()) {
									imported.addAndGet(writeBatch(batch));
								}
							});
		}
		imported.addAndGet(writeBatch(batch)); // whatever did not fill a whole batch

		log.info(
				"Imported {} workations from {}, skipped {}",
				imported.get(),
				properties.location(),
				skipped);
	}

	/**
	 * {@code flush()} sends the pending inserts; {@code clear()} detaches them so the session stops
	 * tracking rows already written. Without the second call the SQL is batched but the heap grows.
	 */
	private int writeBatch(List<Workation> batch) {
		if (batch.isEmpty()) {
			return 0;
		}
		repository.saveAll(batch);
		entityManager.flush();
		entityManager.clear();

		int written = batch.size();
		batch.clear();
		return written;
	}

	/** Charset declared explicitly: the JVM default varies by machine. */
	private Reader openCsv() throws IOException {
		return new InputStreamReader(properties.location().getInputStream(), StandardCharsets.UTF_8);
	}
}
