package com.devmate.stockalarm.service.specification;

import com.devmate.stockalarm.common.config.AlphavantageConfig;
import com.devmate.stockalarm.common.exception.StockReaderException;
import com.devmate.stockalarm.service.implementation.StockReaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class StockReaderServiceImpl implements StockReaderService {

  private static final String ROOT_NAME = "Global Quote";
  private static final String PRICE_NODE = "05. price";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final AlphavantageConfig config;

  public StockReaderServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, AlphavantageConfig config) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.config = config;
  }

  @Override
  public Double getPrice(String symbol) {
    try {
      String json = Objects.requireNonNull(restTemplate.getForObject(config.getUrlForPrice(symbol), String.class));
      JsonNode root = objectMapper.readTree(json);
      return root.path(ROOT_NAME).path(PRICE_NODE).require().asDouble();
    } catch (JsonProcessingException e) {
      throw new StockReaderException(String.format("Cannot read the price for symbol=%s", symbol), e);
    } catch (IllegalArgumentException e) {
      throw new StockReaderException(String.format("Stock with symbol=%s does not exist in our source", symbol), e);
    } catch (RestClientException | NullPointerException e) {
      throw new StockReaderException("Stock reading source is unavailable", e);
    }
  }
}
