package com.workflex.workation.service;

import com.workflex.workation.domain.WorkationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Reads workations for the API.
 *
 * <p>No {@code @Transactional}: Spring Data already runs each repository call in its own transaction,
 * so wrapping a single query would add a proxy without changing behaviour.
 */
@Service
@RequiredArgsConstructor
public class WorkationService {

	private final WorkationRepository repository;
	private final WorkationMapper mapper;

	/** Sorted by key, which is import order: an endpoint with no ORDER BY has no guaranteed order. */
	public List<WorkationDto> findAll() {
		return mapper.toDtos(repository.findAll(Sort.by("id")));
	}
}
