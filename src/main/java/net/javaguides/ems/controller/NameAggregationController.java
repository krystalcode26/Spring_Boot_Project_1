package net.javaguides.ems.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.service.NameAggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/name")
@RequiredArgsConstructor
public class NameAggregationController {

  private final NameAggregationService nameAggregationService;

  @PostMapping("/aggregation")
  public ResponseEntity<NameAggregationResponse> aggregate(
      @Valid @RequestBody NameAggregationRequest request) {
    NameAggregationResponse response = nameAggregationService.aggregate(request);
    return ResponseEntity.ok(response);
  }
}
