package org.imperial.fastquantanalysis.exception;

public class StrategyRunningException extends RuntimeException {

  public StrategyRunningException(String message) {
      super(message);
  }

  public StrategyRunningException(String message, Throwable cause) {
    super(message, cause);
  }
}
