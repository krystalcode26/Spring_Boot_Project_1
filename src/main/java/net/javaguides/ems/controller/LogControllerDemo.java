package net.javaguides.ems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogControllerDemo {
  //create a logger to captures messages
  Logger logger = LoggerFactory.getLogger(LogControllerDemo.class);

  @RequestMapping("/log") public String log(){

    logger.info("Log level information: ");
    return "log";
  }

}
