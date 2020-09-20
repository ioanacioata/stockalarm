package com.devmate.stockalarm.alerts;

import com.devmate.stockalarm.model.Alarm;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Component
public class EmailSender {
  private static Logger logger = LogManager.getLogger(EmailSender.class);

  private final EmailService emailService;

  @Value("${spring.mail.username}")
  private String emailAddress;

  @Autowired
  public EmailSender(EmailService emailService) {
    this.emailService = emailService;
  }

  public void sendMailToAddress(String subject, String body, String email) throws AddressException {
    Email mail;
    mail = DefaultEmail.builder()
                       .from(new InternetAddress(emailAddress))
                       .to(Lists.newArrayList(new InternetAddress(email)))
                       .subject(subject)
                       .body(body)
                       .encoding("UTF-8")
                       .build();
    emailService.send(mail);
  }

  public void sendAlarmNotification(Alarm alarm) {
    String email = alarm.getUser().getEmail();
    String subject = String.format("Stock alarm for %s", alarm.getStockSymbol());
    String body = String.format("Your alarm for the stock %s with target percentage %s has been triggered.\n " +
                                    "The current price is: %s", alarm.getStockSymbol(), alarm.getTarget(), alarm.getCurrentPrice());
    try {
      this.sendMailToAddress(subject, body, email);
    } catch (AddressException e) {
      logger.error(String.format("Cannot send alarms to email %s.", email), e);
    }
  }
}
