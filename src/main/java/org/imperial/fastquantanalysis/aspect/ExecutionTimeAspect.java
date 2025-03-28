package org.imperial.fastquantanalysis.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("execution(* org.imperial.fastquantanalysis..*Service.*(..))")
    public Object executionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("Execution time of {} is {}ms", joinPoint.getSignature().getName(), end - start);
        return result;
    }

    @Around("@annotation(org.imperial.fastquantanalysis.annotation.AsyncTimed)")
    public Object timeAsyncMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        if (result instanceof CompletableFuture<?> future) {
            return future.whenComplete((r, ex) -> {
                long end = System.currentTimeMillis();
                log.info("Execution time of {} is {}ms", joinPoint.getSignature().getName(), end - start);
            });
        } else {
            return result;
        }
    }
}
