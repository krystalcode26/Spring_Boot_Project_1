package net.javaguides.ems.controller;

import jakarta.validation.Valid;
import net.javaguides.ems.dto.NameAggregationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/name")
public class NameNotifyController {

  private static final Logger log = LoggerFactory.getLogger(NameNotifyController.class);

  @PostMapping("/notify")
  public ResponseEntity<Map<String, Object>> notify(@Valid @RequestBody NameAggregationRequest request) {
    log.info("[Upstream module] Received chain notification names={}", request.getName());
    return ResponseEntity.ok(Map.of(
        "status", "received",
        "name", request.getName()
    ));
  }
}
