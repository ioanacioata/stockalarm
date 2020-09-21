package com.devmate.stockalarm.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface GenericMapper<Entity, Dto> {

  Entity toEntity(Dto dto);

  Dto toDto(Entity entity);

  default List<Entity> toEntityList(Collection<Dto> dtoList) {
    return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
  }

  default List<Dto> toDtoList(Collection<Entity> entityList) {
    return entityList.stream().map(this::toDto).collect(Collectors.toList());
  }
}
