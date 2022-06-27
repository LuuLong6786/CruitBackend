package com.tma.recruit.model.mapper;

import org.mapstruct.*;

import java.util.List;

public interface EntityMapper<ENTITY, RESPONSE, REQUEST> {

    RESPONSE toResponse(ENTITY entity);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdDate")
    @Mapping(ignore = true, target = "updatedDate")
    ENTITY toEntity(REQUEST model);

    List<ENTITY> toEntity(List<REQUEST> dtoList);

    List<RESPONSE> toResponse(List<ENTITY> entityList);

    @Named("partialUpdate")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget ENTITY entity, REQUEST model);
}