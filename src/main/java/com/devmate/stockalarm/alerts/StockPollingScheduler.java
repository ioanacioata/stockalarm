package com.devmate.stockalarm.alerts;

import com.devmate.stockalarm.model.Alarm;
import com.devmate.stockalarm.repository.AlarmRepository;
import com.devmate.stockalarm.service.implementation.StockReaderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class StockPollingScheduler {
  private static Logger logger = LogManager.getLogger(StockPollingScheduler.class);

  private final AlarmRepository alarmRepository;
  private final StockReaderService stockReaderService;
  private final EmailSender emailSender;

  private Map<String, List<Alarm>> activeStockAlarms;

  public StockPollingScheduler(AlarmRepository alarmRepository, StockReaderService stockReaderService, EmailSender emailSender) {
    this.alarmRepository = alarmRepository;
    this.stockReaderService = stockReaderService;
    this.emailSender = emailSender;
    this.activeStockAlarms = new ConcurrentHashMap<>(
        alarmRepository.findAllByIsActiveOrderByStockSymbol(true).stream().collect(Collectors.groupingBy(Alarm::getStockSymbol)));
  }

  @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
  public void sendAlarm() {
    logger.info(String.format("Start checking for alarms. Currently there are %d stocks to be checked", activeStockAlarms.size()));
    for (Map.Entry<String, List<Alarm>> entry : activeStockAlarms.entrySet()) {
      Double currentPrice = stockReaderService.getPrice(entry.getKey());
      List<Alarm> alarmsForEmail = retrieveAlarmsForEmailAndUpdateData(entry.getValue(), currentPrice);
      for (Alarm alarm : alarmsForEmail) {
        emailSender.sendAlarmNotification(alarm);
        entry.getValue().remove(alarm);
      }
    }
    logger.info("Finish sending alarms.");
  }

  @Transactional
  List<Alarm> retrieveAlarmsForEmailAndUpdateData(List<Alarm> alarms, Double currentPrice) {
    List<Alarm> alarmsToBeSent = new ArrayList<>();
    for (Alarm alarm : alarms) {
      if (alarm.getCurrentPrice().equals(currentPrice)) {
        continue;
      }
      alarm.setCurrentPrice(currentPrice);
      alarm.setVariance(computeVariancePercentage(alarm));
      if (shouldSendAlert(alarm)) {
        alarmsToBeSent.add(alarm);
        alarm.setIsActive(false);
      }
      alarmRepository.save(alarm);
    }
    return alarmsToBeSent;
  }

  private boolean shouldSendAlert(Alarm alarm) {
    return alarm.getTarget() > 0 ? (alarm.getVariance() >= alarm.getTarget()) : (alarm.getVariance() <= alarm.getTarget());
  }

  private double computeVariancePercentage(Alarm alarm) {
    double deltaPercentage = alarm.getCurrentPrice() * 100 / alarm.getInitialPrice();
    return deltaPercentage - 100;
  }

  public void addNewActiveAlarm(Alarm alarm) {
    List<Alarm> list = activeStockAlarms.getOrDefault(alarm.getStockSymbol(), new ArrayList<>());
    list.add(alarm);
    activeStockAlarms.put(alarm.getStockSymbol(), list);
  }

  public void removeFromActiveAlarms(Alarm alarm) {
    logger.info(String.format("Removing alarm %s from active alarms list.", alarm));
    if (!activeStockAlarms.containsKey(alarm.getStockSymbol())) {
      return;
    }
    activeStockAlarms.get(alarm.getStockSymbol()).remove(alarm);
    if (activeStockAlarms.get(alarm.getStockSymbol()).isEmpty()) {
      activeStockAlarms.remove(alarm.getStockSymbol());
    }
  }

  public void updateAlarm(Alarm alarm) {
    removeFromActiveAlarms(alarm);
    addNewActiveAlarm(alarm);
  }
}
