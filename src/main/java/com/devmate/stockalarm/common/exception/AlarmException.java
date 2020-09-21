package com.devmate.stockalarm.common.exception;

public class AlarmException extends ServiceException {
  private static final long serialVersionUID = 2316880679554378702L;

  public AlarmException(String message) {
    super(message);
  }

  public AlarmException(String message, Throwable cause) {
    super(message, cause);
  }
}
