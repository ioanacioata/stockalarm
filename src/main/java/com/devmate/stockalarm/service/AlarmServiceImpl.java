package com.devmate.stockalarm.service;

import com.devmate.stockalarm.common.exception.ServiceException;
import com.devmate.stockalarm.dto.AlarmDto;
import com.devmate.stockalarm.mapper.AlarmMapper;
import com.devmate.stockalarm.model.Alarm;
import com.devmate.stockalarm.model.User;
import com.devmate.stockalarm.repository.AlarmRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlarmServiceImpl implements AlarmService {

  private final AlarmRepository repository;

  private final UserService userService;

  private final AlarmMapper alarmMapper;

  public AlarmServiceImpl(AlarmRepository repository, UserService userService, AlarmMapper alarmMapper) {
    this.repository = repository;
    this.userService = userService;
    this.alarmMapper = alarmMapper;
  }

  @Override
  public List<AlarmDto> getAllForUser() {
    User loggedInUser = userService.getLoggedInUser();
    return alarmMapper.toDtoList(loggedInUser.getAlarms());
  }

  @Override
  public void insert(AlarmDto alarmDto) {
    Alarm alarm = alarmMapper.toEntity(alarmDto);
    alarm.setIsActive(true);
    User loggedInUser = userService.getLoggedInUser();
    alarm.setUser(loggedInUser);
    alarm.setId(null);
    try {
      repository.save(alarm);
    } catch (DataIntegrityViolationException e) {
      throw new ServiceException("Alarm is invalid. You already have an alarm for this stock symbol.");
    }
  }

  @Override
  public void update(Long id, AlarmDto alarmDto) {
    Alarm receivedAlarm = alarmMapper.toEntity(alarmDto);
    Optional<Alarm> existingAlarm = repository.findById(id);
    if (existingAlarm.isEmpty()) {
      throw new ServiceException("Alarm does not exist");
    }
    Alarm alarm = existingAlarm.get();
    alarm.setIsActive(receivedAlarm.getIsActive());
    alarm.setTarget(receivedAlarm.getTarget());
    repository.save(alarm);
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id);
  }

  @Override
  public AlarmDto findById(Long id) {
    Optional<Alarm> existingAlarm = repository.findById(id);
    if (existingAlarm.isEmpty()) {
      throw new ServiceException("Alarm does not exist");
    }
    return alarmMapper.toDto(existingAlarm.get());
  }
}
