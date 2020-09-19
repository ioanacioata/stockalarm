package com.devmate.stockalarm.common.exception;

public class UserException extends ServiceException {

  private static final long serialVersionUID = -2578787862953901403L;

  public UserException(String message) {
    super(message);
  }

  public UserException(String message, Throwable cause) {
    super(message, cause);
  }
}
