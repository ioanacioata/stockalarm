package com.devmate.stockalarm.service.specification;

import com.devmate.stockalarm.alerts.StockPollingScheduler;
import com.devmate.stockalarm.common.exception.AlarmException;
import com.devmate.stockalarm.dto.AlarmDto;
import com.devmate.stockalarm.mapper.AlarmMapper;
import com.devmate.stockalarm.model.Alarm;
import com.devmate.stockalarm.model.User;
import com.devmate.stockalarm.repository.AlarmRepository;
import com.devmate.stockalarm.service.implementation.AlarmService;
import com.devmate.stockalarm.service.implementation.StockReaderService;
import com.devmate.stockalarm.service.implementation.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlarmServiceImpl implements AlarmService {

  private final AlarmRepository repository;

  private final UserService userService;

  private final AlarmMapper alarmMapper;

  private final StockReaderService stockReaderService;

  private final StockPollingScheduler stockPollingScheduler;

  public AlarmServiceImpl(AlarmRepository repository, UserService userService, AlarmMapper alarmMapper, StockReaderService stockReaderService,
                          StockPollingScheduler stockPollingScheduler) {
    this.repository = repository;
    this.userService = userService;
    this.alarmMapper = alarmMapper;
    this.stockReaderService = stockReaderService;
    this.stockPollingScheduler = stockPollingScheduler;
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
    Double price = stockReaderService.getPrice(alarm.getStockSymbol());
    alarm.setInitialPrice(price);
    alarm.setCurrentPrice(price);
    alarm.setVariance(0d);
    try {
      repository.save(alarm);
    } catch (DataIntegrityViolationException e) {
      throw new AlarmException("Alarm is invalid. You already have an alarm for this stock symbol.");
    }
    stockPollingScheduler.addNewActiveAlarm(alarm);
  }

  @Override
  public void update(Long id, AlarmDto alarmDto) {
    Alarm receivedAlarm = alarmMapper.toEntity(alarmDto);
    Optional<Alarm> existingAlarm = repository.findById(id);
    if (existingAlarm.isEmpty()) {
      throw new AlarmException("Alarm does not exist");
    }
    Alarm alarm = existingAlarm.get();
    alarm.setIsActive(receivedAlarm.getIsActive());
    alarm.setTarget(receivedAlarm.getTarget());
    repository.save(alarm);

    if (alarm.getIsActive()) {
      stockPollingScheduler.updateAlarm(alarm);
    } else {
      stockPollingScheduler.removeFromActiveAlarms(alarm);
    }
  }

  @Override
  public void delete(Long id) {
    Optional<Alarm> existingAlarm = repository.findById(id);
    if (existingAlarm.isEmpty()) {
      throw new AlarmException("Alarm does not exist");
    }
    Alarm alarm = existingAlarm.get();
    if (findById(id).getIsActive()) {
      stockPollingScheduler.removeFromActiveAlarms(alarm);
    }
    repository.delete(alarm);
  }

  @Override
  public AlarmDto findById(Long id) {
    Optional<Alarm> existingAlarm = repository.findById(id);
    if (existingAlarm.isEmpty()) {
      throw new AlarmException("Alarm does not exist");
    }
    return alarmMapper.toDto(existingAlarm.get());
  }
}
