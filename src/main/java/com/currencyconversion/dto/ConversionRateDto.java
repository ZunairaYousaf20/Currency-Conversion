package com.currencyconversion.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ConversionRateDto {

    private String result;
    private String base_code;
    private Map<String, BigDecimal> conversion_rates;
}
