package com.currencyconversion.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BillCalculationRequest {

    private RequestedUser user;
    private List<RequestedItems> items;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal totalAmount;
}
