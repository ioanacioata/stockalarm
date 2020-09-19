package com.devmate.stockalarm.service;

import com.devmate.stockalarm.common.exception.StockReaderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class StockReaderServiceImpl implements StockReaderService {

  public static final String ROOT_NAME = "Global Quote";
  public static final String PRICE_NODE = "05. price";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("$(stock.data.api.url.format)")
  private String urlFormat;
  @Value("$(stock.data.api.key)")
  private String apiKey;

  public StockReaderServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public Double getPrice(String symbol) {
    String url = String.format(urlFormat, symbol, apiKey);
    String json = restTemplate.getForObject(url, String.class);
    try {
      JsonNode root = objectMapper.readTree(Objects.requireNonNull(json));
      return root.path(ROOT_NAME).path(PRICE_NODE).asDouble();
    } catch (JsonProcessingException e) {
      throw new StockReaderException(String.format("Cannot read the price for symbol=%s", symbol), e);
    }
  }
}
