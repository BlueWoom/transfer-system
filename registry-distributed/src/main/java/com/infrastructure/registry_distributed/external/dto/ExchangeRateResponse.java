package com.infrastructure.registry_distributed.external.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ExchangeRateResponse(BigDecimal amount,
                                   String base,
                                   LocalDate date,
                                   Map<String, BigDecimal> rates) {}
