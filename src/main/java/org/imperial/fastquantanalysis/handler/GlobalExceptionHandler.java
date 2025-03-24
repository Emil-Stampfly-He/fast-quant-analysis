package org.imperial.fastquantanalysis.handler;

import org.imperial.fastquantanalysis.exception.StrategyRunningException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler of global exception
 *
 * @author Emil S. He
 * @since 2025-03-23
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StrategyRunningException.class)
    public ResponseEntity<?> handleStrategyRunningException(StrategyRunningException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Strategy running error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
