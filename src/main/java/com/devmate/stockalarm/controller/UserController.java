package com.devmate.stockalarm.controller;

import com.devmate.stockalarm.common.exception.ServiceException;
import com.devmate.stockalarm.dto.UserDto;
import com.devmate.stockalarm.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.devmate.stockalarm.controller.ThymeLeafConstants.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class UserController {
  private static Logger logger = LogManager.getLogger(UserController.class);

  private final UserService userService;
  private final AuthenticationManager authenticationManager;

  public UserController(UserService userService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String getLoginPage(Model model) {
    model.addAttribute(LOGIN_PAGE_EMAIL_ATTRIBUTE, "");
    model.addAttribute(LOGIN_PAGE_PASSWORD_ATTRIBUTE, "");
    return LOGIN_PAGE;
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(@ModelAttribute("email") String email, @ModelAttribute("password") String password, HttpServletRequest req, Model model) {
    try {
      userService.validate(email, password);
      authenticateUser(req, email, password);
      return "redirect:/";
    } catch (ServiceException e) {
      logger.error("Received exception:", e);
      model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
      model.addAttribute(LOGIN_PAGE_EMAIL_ATTRIBUTE, email);
      model.addAttribute(LOGIN_PAGE_PASSWORD_ATTRIBUTE, password);
      return LOGIN_PAGE;
    }
  }

  @RequestMapping(value = "/sign-up", method = RequestMethod.GET)
  public String getSignUpPage(Model model) {
    model.addAttribute(SIGN_UP_PAGE_USER_ATTRIBUTE, new UserDto());
    return SIGNUP_PAGE;
  }

  @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
  public String signUp(@ModelAttribute("user") UserDto user, HttpServletRequest req, Model model) {
    try {
      UserDto savedUser = userService.save(user);
      authenticateUser(req, savedUser.getEmail(), savedUser.getPassword());
      return "redirect:/";
    } catch (ServiceException e) {
      logger.error("Received exception:", e);
      model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
      model.addAttribute(SIGN_UP_PAGE_USER_ATTRIBUTE, user);
      return SIGNUP_PAGE;
    }
  }

  private void authenticateUser(HttpServletRequest req, String email, String password) {
    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(email, password);
    Authentication auth = authenticationManager.authenticate(authReq);
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    HttpSession session = req.getSession(true);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    logger.info(String.format("User %s is authenticated", email));
  }
}
