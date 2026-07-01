package net.javaguides.ems.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("execution(* net.javaguides.ems.service.impl.StudentServiceImpl.*(..))")
  public void studentServiceMethods() {}

  @Pointcut("execution(* net.javaguides.ems.service.impl.NameAggregationServiceImpl.*(..))")
  public void nameAggregationServiceMethods() {}

  @Pointcut("execution(* net.javaguides.ems.client.*.*(..))")
  public void integrationClientMethods() {}

  @Pointcut("execution(* net.javaguides.ems.service.NameAggregationIntegrationExecutor.*(..))")
  public void nameAggregationIntegrationMethods() {}

  @Before("studentServiceMethods() || nameAggregationServiceMethods() || integrationClientMethods() || nameAggregationIntegrationMethods()")
  public void logBefore(JoinPoint jp) {
    log.info("[Before] {} args={}", jp.getSignature().toShortString(), jp.getArgs());
  }

  @AfterReturning(
      pointcut = "studentServiceMethods() || nameAggregationServiceMethods() || integrationClientMethods() || nameAggregationIntegrationMethods()",
      returning = "result")
  public void logAfterReturning(JoinPoint jp, Object result) {
    log.info("[After Returning] {} => {}", jp.getSignature().toShortString(), result);
  }

  @AfterThrowing(
      pointcut = "studentServiceMethods() || nameAggregationServiceMethods() || integrationClientMethods() || nameAggregationIntegrationMethods()",
      throwing = "ex")
  public void logAfterThrowing(JoinPoint jp, Exception ex) {
    log.warn("[After Throwing] {} threw: {}", jp.getSignature().toShortString(), ex.getMessage());
  }

  @Around("studentServiceMethods() || nameAggregationServiceMethods() || integrationClientMethods() || nameAggregationIntegrationMethods()")
  public Object measureTime(ProceedingJoinPoint jp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = jp.proceed();
    long duration = System.currentTimeMillis() - start;
    log.info("[Around] {} took {}ms", jp.getSignature().toShortString(), duration);
    return result;
  }
}
