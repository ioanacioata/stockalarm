package com.devmate.stockalarm.common.exception;

public class ServiceException extends RuntimeException {
  private static final long serialVersionUID = 8383500167893078269L;

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
