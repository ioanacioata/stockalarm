package com.devmate.stockalarm.mapper;

import com.devmate.stockalarm.common.exception.MappingException;
import com.devmate.stockalarm.dto.AlarmDto;
import com.devmate.stockalarm.model.Alarm;
import org.springframework.stereotype.Component;

@Component
public class AlarmMapper implements GenericMapper<Alarm, AlarmDto> {
  @Override
  public Alarm toEntity(AlarmDto dto) {
    if (dto == null) {
      return null;
    }
    Alarm entity = new Alarm();

    entity.setId(dto.getId());
    entity.setCurrentPrice(dto.getCurrentPrice());
    entity.setInitialPrice(dto.getInitialPrice());
    try {
      Double target = Double.parseDouble(dto.getTarget());
      entity.setTarget(target);
    } catch (NumberFormatException e) {
      throw new MappingException("The target value should be a number");
    }
    entity.setVariance(dto.getVariance());
    entity.setStockSymbol(dto.getStockSymbol());
    entity.setIsActive(dto.getIsActive());
    return entity;
  }

  @Override
  public AlarmDto toDto(Alarm entity) {
    if (entity == null) {
      return null;
    }
    AlarmDto dto = new AlarmDto();
    dto.setId(entity.getId());
    dto.setCurrentPrice(entity.getCurrentPrice());
    dto.setInitialPrice(entity.getInitialPrice());
    dto.setTarget(String.valueOf(entity.getTarget()));
    dto.setVariance(entity.getVariance());
    dto.setStockSymbol(entity.getStockSymbol());
    dto.setIsActive(entity.getIsActive());
    return dto;
  }
}
