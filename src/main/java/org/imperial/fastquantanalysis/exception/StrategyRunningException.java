package org.imperial.fastquantanalysis.exception;

/**
 * Strategy running exception
 *
 * @author Emil S. He
 * @since 2025-03-23
 */
public class StrategyRunningException extends RuntimeException {

  public StrategyRunningException(String message) {
      super(message);
  }

  public StrategyRunningException(String message, Throwable cause) {
    super(message, cause);
  }
}
