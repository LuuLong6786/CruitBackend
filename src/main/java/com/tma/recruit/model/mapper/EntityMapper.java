package com.tma.recruit.model.mapper;

import org.mapstruct.*;

import java.util.List;


public interface EntityMapper<ENTITY, RESPONSE, REQUEST> {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdDate")
    @Mapping(ignore = true, target = "updatedDate")
    ENTITY toEntity(REQUEST model);

    @Mapping(ignore = true, target = "createdBy.createdBy")
    @Mapping(ignore = true, target = "createdBy.updatedBy")
    @Mapping(ignore = true, target = "updatedBy.createdBy")
    @Mapping(ignore = true, target = "updatedBy.updatedBy")
    RESPONSE toResponse(ENTITY entity);

    List<ENTITY> toEntity(List<REQUEST> dtoList);

    List<RESPONSE> toResponse(List<ENTITY> entityList);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget ENTITY entity, REQUEST model);
}