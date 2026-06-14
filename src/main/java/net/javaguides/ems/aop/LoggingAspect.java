package net.javaguides.ems.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
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
  
  // this is a pointcut expression that matches all methods in the StudentServiceImpl class
  public void studentServiceMethods() {}

  @Before("studentServiceMethods()")
  public void logBefore(JoinPoint jp) {
    log.info("[Before] {} args={}", jp.getSignature().toShortString(), jp.getArgs());
  }

  @AfterReturning(pointcut = "studentServiceMethods()", returning = "result")
  public void logAfterReturning(JoinPoint jp, Object result) {
    log.info("[After Returning] {} => {}", jp.getSignature().toShortString(), result);
  }

  @AfterThrowing(pointcut = "studentServiceMethods()", throwing = "ex")
  public void logAfterThrowing(JoinPoint jp, Exception ex) {
    log.warn("[After Throwing] {} threw: {}", jp.getSignature().toShortString(), ex.getMessage());
  }

  @After("studentServiceMethods()")
  public void logAfter(JoinPoint jp) {
    log.info("[After] {} completed", jp.getSignature().toShortString());
  }

  @Around("studentServiceMethods()")
  public Object measureTime(ProceedingJoinPoint jp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = jp.proceed();
    long duration = System.currentTimeMillis() - start;
    log.info("[Around] {} took {}ms", jp.getSignature().toShortString(), duration);
    return result;
  }
}
