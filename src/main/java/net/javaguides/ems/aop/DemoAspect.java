package net.javaguides.ems.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DemoAspect {

  private static final Logger log = LoggerFactory.getLogger(DemoAspect.class);

  @Pointcut("execution(* net.javaguides.ems.service.DemoService.*(..))")
  public void demoMethods() {}

  @Before("demoMethods()")
  public void logBefore(JoinPoint jp) {
    log.info("[DEMO - BEFORE] aspect fired for: {}", jp.getSignature().toShortString());
  }
}
