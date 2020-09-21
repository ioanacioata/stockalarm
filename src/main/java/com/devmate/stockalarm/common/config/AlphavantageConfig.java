package com.devmate.stockalarm.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlphavantageConfig {

  @Value("${stock.data.api.url}")
  private String url;
  @Value("${stock.data.api.key}")
  private String apiKey;

  public String getUrlForPrice(String symbol) {
    return String.format("%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", url, symbol, apiKey);
  }
}
