package com.currencyconversion.response;

import lombok.*;

import java.math.BigDecimal;

@Data
public class BillCalculationResponse {

    private BigDecimal totalPayableAmount;
}
