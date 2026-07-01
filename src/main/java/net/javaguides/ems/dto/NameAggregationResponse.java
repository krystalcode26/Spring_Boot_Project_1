package net.javaguides.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NameAggregationResponse {

  private List<String> name = new ArrayList<>();

  /** Set when upstream/downstream integration degrades but local aggregation still succeeds. */
  private String warning;
}
