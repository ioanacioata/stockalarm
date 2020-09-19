package com.devmate.stockalarm.controller;

import com.devmate.stockalarm.common.exception.ServiceException;
import com.devmate.stockalarm.dto.AlarmDto;
import com.devmate.stockalarm.service.AlarmService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.devmate.stockalarm.controller.ThymeLeafConstants.*;

@Controller
@RequestMapping("/alarms")
public class AlarmController {
  private static Logger logger = LogManager.getLogger(AlarmController.class);

  private final AlarmService service;

  public AlarmController(AlarmService service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public String getDisplayAlarmsPage(Model model) {
    List<AlarmDto> alarms = service.getAllForUser();
    logger.info(String.format("Display Alarms: User has %d alarms.", alarms.size()));
    model.addAttribute(ALARMS_ATTRIBUTE, alarms);
    return ALARMS_PAGE;
  }

  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public String getAddAlarmPage(Model model) {
    model.addAttribute(ALARM_ATTRIBUTE, new AlarmDto());
    return ADD_ALARM_PAGE;
  }

  @RequestMapping(method = RequestMethod.POST)
  public String add(@ModelAttribute(value = "newAlarm") AlarmDto alarmDto, Model model) {
    logger.info("Received request for add alarm.");
    try {
      service.insert(alarmDto);
      return "redirect:/";
    } catch (ServiceException e) {
      logger.error("Received exception:", e);
      model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
      model.addAttribute(ALARM_ATTRIBUTE, alarmDto);
      return ADD_ALARM_PAGE;
    }
  }

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String getEditAlarmPage(HttpServletRequest request, Model model) {
    Long id = Long.valueOf(request.getParameter("id"));
    AlarmDto alarm = service.findById(id);
    model.addAttribute(ALARM_ATTRIBUTE, alarm);
    return EDIT_ALARM_PAGE;
  }

  @RequestMapping(value = "/edit", method = RequestMethod.POST)
  public String update(@ModelAttribute(value = "newAlarm") AlarmDto alarmDto, Model model) {
    logger.info("Received request for update alarm");
    try {
      service.update(alarmDto.getId(), alarmDto);
      return "redirect:/";
    } catch (ServiceException e) {
      logger.error("Received exception:", e);
      model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
      model.addAttribute(ALARM_ATTRIBUTE, alarmDto);
      return EDIT_ALARM_PAGE;
    }
  }

  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public String delete(@ModelAttribute(value = "id") Long id) {
    logger.info("Received request for delete alarm");
    service.delete(id);
    return "redirect:/";
  }
}
