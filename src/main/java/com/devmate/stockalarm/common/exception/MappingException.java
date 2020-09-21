package com.devmate.stockalarm.common.exception;

public class MappingException extends ServiceException {
  private static final long serialVersionUID = 2678334634408201138L;

  public MappingException(String message) {
    super(message);
  }

  public MappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
