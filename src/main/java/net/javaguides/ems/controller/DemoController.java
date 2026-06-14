package net.javaguides.ems.controller;

import lombok.AllArgsConstructor;
import net.javaguides.ems.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/demo")
public class DemoController {

  private final DemoService demoService;

  @GetMapping("/problem-a")
  public String problemA() {
    return demoService.triggerProblemA();
  }

  @GetMapping("/problem-b")
  public String problemB() {
    return demoService.triggerProblemB();
  }
}
