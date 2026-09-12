package com.workflex.workation.service;

import com.workflex.workation.domain.Workation;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entity to API contract. The implementation is generated at compile time. */
@Mapper(componentModel = "spring")
public interface WorkationMapper {

	@Mapping(target = "id", source = "workationId")
	@Mapping(target = "origin.name", source = "originCountry")
	@Mapping(target = "origin.isoCode", source = "originIsoCode")
	@Mapping(target = "destination.name", source = "destinationCountry")
	@Mapping(target = "destination.isoCode", source = "destinationIsoCode")
	WorkationDto toDto(Workation workation);

	List<WorkationDto> toDtos(List<Workation> workations);
}
