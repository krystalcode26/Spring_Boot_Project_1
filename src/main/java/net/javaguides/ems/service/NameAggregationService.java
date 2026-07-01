package net.javaguides.ems.service;

import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;

//keep flexibility in the service layer separate from the business logic
public interface NameAggregationService {

  NameAggregationResponse aggregate(NameAggregationRequest request);
}
