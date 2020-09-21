package com.devmate.stockalarm.service;

import com.devmate.stockalarm.common.config.AlphavantageConfig;
import com.devmate.stockalarm.common.exception.StockReaderException;
import com.devmate.stockalarm.service.implementation.StockReaderService;
import com.devmate.stockalarm.service.specification.StockReaderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class StockReaderServiceTest {

  public static final String STOCK_SYMBOL = "stock";
  private final String ALPHAVANTAGE_URL = "https://test";

  private StockReaderService service;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private AlphavantageConfig config;

  @BeforeEach
  void setUp() {
    initMocks(this);
    service = new StockReaderServiceImpl(restTemplate, new ObjectMapper(), config);
    when(config.getUrlForPrice("stock")).thenReturn(ALPHAVANTAGE_URL);
  }

  @Test
  void getPrice_whenReturnsValidData_shouldReturnThePrice() {
    // given
    Double expected = 252.5300;
    String data = "{\n" +
        "    \"Global Quote\": {\n" +
        "        \"01. symbol\": \"stock\",\n" +
        "        \"02. open\": \"258.4050\",\n" +
        "        \"03. high\": \"259.2000\",\n" +
        "        \"04. low\": \"250.0500\",\n" +
        "        \"05. price\": \"252.5300\",\n" +
        "        \"06. volume\": \"28130767\",\n" +
        "        \"07. latest trading day\": \"2020-09-18\",\n" +
        "        \"08. previous close\": \"254.8200\",\n" +
        "        \"09. change\": \"-2.2900\",\n" +
        "        \"10. change percent\": \"-0.8987%\"\n" +
        "    }\n" +
        "}";
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenReturn(data);

    // when
    Double actual = service.getPrice(STOCK_SYMBOL);

    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getPrice_whenReturnNullObject_shouldThrowStockReaderException() {
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenReturn(null);

    assertThatExceptionOfType(StockReaderException.class).isThrownBy(() -> service.getPrice(STOCK_SYMBOL));
  }

  @Test
  void getPrice_whenResourceIsUnavailableAndThrowsRestClientException_shouldThrowStockReaderException() {
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenThrow(RestClientException.class);

    assertThatExceptionOfType(StockReaderException.class).isThrownBy(() -> service.getPrice(STOCK_SYMBOL));
  }

  @Test
  void getPrice_whenReturnsEmptyBody_shouldThrowStockReaderException() {
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenReturn("{}");

    assertThatExceptionOfType(StockReaderException.class).isThrownBy(() -> service.getPrice(STOCK_SYMBOL));
  }

  @Test
  void getPrice_whenPriceNodeIsMissing_shouldThrowStockReaderException() {
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenReturn("{\"Global Quote\": {}}");

    assertThatExceptionOfType(StockReaderException.class).isThrownBy(() -> service.getPrice(STOCK_SYMBOL));
  }

  @Test
  void getPrice_whenReturnsInvalidJson_shouldThrowStockReaderException() {
    when(restTemplate.getForObject(ALPHAVANTAGE_URL, String.class)).thenReturn("{Global Quote: {}}");

    assertThatExceptionOfType(StockReaderException.class).isThrownBy(() -> service.getPrice(STOCK_SYMBOL));
  }
}