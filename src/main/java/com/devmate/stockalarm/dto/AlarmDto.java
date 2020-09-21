package com.devmate.stockalarm.dto;

import lombok.Data;

@Data
public class AlarmDto {

  private Long id;

  private Double initialPrice;

  private Double currentPrice;

  private Double variance;

  private String target;

  private String stockSymbol;

  private Boolean isActive;
}
