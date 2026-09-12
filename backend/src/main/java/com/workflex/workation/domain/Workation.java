package com.workflex.workation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A workation stay, as persisted.
 *
 * <p>No {@code equals}/{@code hashCode}: an id-based hash would change on persist, so the entity
 * would get lost inside a {@code HashSet}.
 */
@Entity
@Table(
		name = "workation",
		indexes = @Index(name = "ux_workation_id", columnList = "workation_id", unique = true))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Workation {

	/** Synthetic key: SEQUENCE rather than IDENTITY so Hibernate can batch the inserts. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workation_seq")
	@SequenceGenerator(name = "workation_seq", sequenceName = "workation_seq", allocationSize = 500)
	private Long id;

	/** Business identifier from the CSV, kept unique but never used as the primary key. */
	@Column(name = "workation_id", nullable = false)
	private String workationId;

	@Column(nullable = false)
	private String employee;

	@Column(nullable = false)
	private String originCountry;

	@Column(nullable = false, length = 2)
	private String originIsoCode;

	@Column(nullable = false)
	private String destinationCountry;

	@Column(nullable = false, length = 2)
	private String destinationIsoCode;

	/** {@code start} and {@code end} are SQL reserved words. */
	@Column(name = "start_date", nullable = false)
	private LocalDate start;

	@Column(name = "end_date", nullable = false)
	private LocalDate end;

	@Column(nullable = false)
	private int workingDays;

	/** STRING, not the ORDINAL default: reordering the enum must not reinterpret stored rows. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private RiskLevel risk;
}
