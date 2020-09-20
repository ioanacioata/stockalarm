package com.devmate.stockalarm.service.implementation;

import com.devmate.stockalarm.common.exception.UserException;
import com.devmate.stockalarm.dto.UserDto;
import com.devmate.stockalarm.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  UserDto save(UserDto user) throws UserException;

  void validate(String email, String password);

  User getLoggedInUser();
}