package com.currencyconversion.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RequestedItems {

    private Long itemId;
    private String category;
    private BigDecimal price;
    private int quantity;
}
