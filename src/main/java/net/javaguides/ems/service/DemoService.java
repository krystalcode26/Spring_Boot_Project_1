package net.javaguides.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

  private static final Logger log = LoggerFactory.getLogger(DemoService.class);

  // Problem A: aspect cannot reach private methods
  public String triggerProblemA() {
    log.info("triggerProblemA() - called from outside, aspect should fire here");
    privateValidate();
    return "Problem A done - privateValidate was NOT intercepted";
  }

  private void privateValidate() {
    log.info("privateValidate() running - aspect did NOT fire (private method)");
  }

  // Problem B: internal this.method() bypasses the Spring proxy
  public String triggerProblemB() {
    log.info("triggerProblemB() - aspect fires here (public, called from outside)");
    this.publicInternalMethod();
    return "Problem B done - publicInternalMethod was NOT intercepted";
  }

  public void publicInternalMethod() {
    log.info("publicInternalMethod() running - but aspect did NOT fire for this method");
  }
}
