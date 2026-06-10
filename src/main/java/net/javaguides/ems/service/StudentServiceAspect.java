package net.javaguides.ems.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StudentServiceAspect {

  private static final Logger log = LoggerFactory.getLogger(StudentServiceAspect.class);

  //@Pointcut - starting point (package path)
  // * anything within
  // WHERE : the StudentServiceImpl package will
  // affects the whole @Pointcut logic later
  @Pointcut("execution(* net.javaguides.ems.service.impl.StudentServiceImpl.*(..))")
  //the Pointcut name should be the same or it won't do anything
  //the name here is serviceMethods
  public void serviceMethods(){}

  // Runs before every service method - logs method name and arguments
  // BEFORE all StudentServiceImpl.java methods, logs info here first
//  @Before("serviceMethods()")
//  public void logBefore(JoinPoint jp){
//    //get method signature to store string
//    log.info("[Before] {}", jp.getSignature().toShortString());
//
//  }

  // Runs after a service method returns successfully - logs the return value
  // Return the result of object result -> getAllStudents() result
  @AfterReturning(pointcut = "serviceMethods()", returning = "result")
  public void logAfterReturning(JoinPoint jp, Object result){
    log.info("[AFTER RETURNING] {} => {}", jp.getSignature().toShortString(), result);
  }

//  // Runs when a service method throws an exception - logs the error
//  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
//  public void logAfterThrowing(JoinPoint jp, Exception ex){
//    log.warn("[After Throwing] {} threw: {}", jp.getSignature().toShortString(), ex.getMessage());
//  }
//
//  // Runs after a service method regardless of outcome (like finally)
//  @After("serviceMethods()")
//  public void logAfter(JoinPoint jp){
//    log.info("[AFTER] {} completed", jp.getSignature().toShortString());
//  }
//
//  // Wraps every service method  - measures and logs execution time
    /*
    * Wraps around getAllEmployee() method
    * start timer
    * jp.proceed() -> execute within the original method, which is the first line of  getAllEmployee()
    * jumps back to getAllEmployee() execute all duration, log.info, return after that method
    * */
//  @Around("serviceMethods()")
//  public Object measureTime(ProceedingJoinPoint jp) throws Throwable{
//    long start = System.currentTimeMillis();
//    Object result = jp.proceed(); //jp.proceed() - API to let method continue
//    long duration = System.currentTimeMillis() - start;
//    log.info("[AROUND] {} took {}ms", jp.getSignature().toShortString(), duration);
//    return result;
//  }

}
