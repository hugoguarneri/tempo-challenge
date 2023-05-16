package com.tenpo.challenge.mapper;

import com.tenpo.challenge.dto.SumDto;
import com.tenpo.challenge.entity.Sum;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SumMapper {

    SumDto toDto(Sum sum);

    Sum toModel(SumDto sumDto);
}
