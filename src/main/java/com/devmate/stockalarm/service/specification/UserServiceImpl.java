package com.devmate.stockalarm.service.specification;

import com.devmate.stockalarm.common.exception.UserException;
import com.devmate.stockalarm.dto.UserDto;
import com.devmate.stockalarm.mapper.UserMapper;
import com.devmate.stockalarm.model.User;
import com.devmate.stockalarm.repository.UserRepository;
import com.devmate.stockalarm.service.implementation.UserService;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) throws UserException {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserDto save(UserDto user) throws UserException {
    if (userRepository.findFirstByEmail(user.getEmail()) != null) {
      throw new UserException(String.format("User with email %s already exists.", user.getEmail()));
    }
    User save = userRepository.save(userMapper.toEntity(user));
    return userMapper.toDto(save);
  }

  @Override
  public void validate(String email, String password) throws UserException {
    User retrievedUser = userRepository.findFirstByEmail(email);
    if (retrievedUser == null || password == null || !password.equals(retrievedUser.getPassword())) {
      throw new UserException("Invalid credentials");
    }
  }

  private User getByEmail(String email) throws UserException {
    User user = userRepository.findFirstByEmail(email);
    if (user == null) {
      throw new UserException(String.format("User with email %s does not exist.", email));
    }
    return user;
  }

  @SneakyThrows
  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return userMapper.toDto(getByEmail(s));
  }

  @Override
  public User getLoggedInUser() throws UserException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        return getByEmail(username);
      }
    }
    throw new UserException("No user logged in.");
  }
}
