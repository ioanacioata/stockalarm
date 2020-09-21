package com.devmate.stockalarm.common.exception;

public class StockReaderException extends ServiceException {
  private static final long serialVersionUID = 3795852942071136913L;

  public StockReaderException(String message) {
    super(message);
  }

  public StockReaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
