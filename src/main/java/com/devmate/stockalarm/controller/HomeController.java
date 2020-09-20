package com.devmate.stockalarm.controller;

import com.devmate.stockalarm.common.exception.UserException;
import com.devmate.stockalarm.model.User;
import com.devmate.stockalarm.service.implementation.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

  private static Logger logger = LogManager.getLogger(HomeController.class);
  private final UserService userService;

  public HomeController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String getHomePage() {
    try {
      User loggedInUser = userService.getLoggedInUser();
      logger.info(String.format("User %s is logged in.", loggedInUser));
      return "redirect:/alarms";
    } catch (UserException e) {
      return "redirect:/login";
    }
  }
}