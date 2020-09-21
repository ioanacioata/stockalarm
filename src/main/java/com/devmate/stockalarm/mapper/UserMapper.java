package com.devmate.stockalarm.mapper;

import com.devmate.stockalarm.dto.UserDto;
import com.devmate.stockalarm.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements GenericMapper<User, UserDto> {

  @Override
  public User toEntity(UserDto dto) {
    if (dto == null) {
      return null;
    }
    User entity = new User();
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmail(dto.getEmail());
    entity.setPassword(dto.getPassword());
    return entity;
  }

  @Override
  public UserDto toDto(User entity) {
    if (entity == null) {
      return null;
    }
    UserDto dto = new UserDto();
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmail(entity.getEmail());
    dto.setPassword(entity.getPassword());
    return dto;
  }
}
