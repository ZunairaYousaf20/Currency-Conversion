package com.currencyconversion.service.Impl;

import com.currencyconversion.dto.*;
import com.currencyconversion.request.BillCalculationRequest;
import com.currencyconversion.request.RequestedItems;
import com.currencyconversion.request.RequestedUser;
import com.currencyconversion.response.BillCalculationResponse;
import com.currencyconversion.service.BillCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillCalculationServiceImpl implements BillCalculationService {

    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String AFFILIATE = "AFFILIATE";
    private static final String REGISTERED_CUSTOMER = "CUSTOMER";
    private static final String SUCCESS = "success";

    @Value("${api.base.url}")
    private String API_BASE_URL;

    @Value("${api.key}")
    private String API_KEY;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public BillCalculationResponse calculatePayableAmount(BillCalculationRequest billCalculationRequest) {
        BigDecimal calculateDiscount = calculateAndApplyDiscount(billCalculationRequest);

        //Calculating payable amount from Source currency to target currency
        BigDecimal currencyConversionRate = currencyConversionRate(billCalculationRequest.getSourceCurrency(),
                billCalculationRequest.getTargetCurrency());

        BillCalculationResponse response = new BillCalculationResponse();
        response.setTotalPayableAmount(calculateDiscount.multiply(currencyConversionRate));
        return response;
    }

    private BigDecimal calculateAndApplyDiscount(BillCalculationRequest billCalculationRequest) {
        RequestedUser requestedUser = billCalculationRequest.getUser();
        BigDecimal totalAmount = billCalculationRequest.getTotalAmount();


        if (requestedUser != null) {
            BigDecimal percentageDiscount = BigDecimal.ZERO;

            //
            BigDecimal nonGroceryItemsAmount = amountByItemType(billCalculationRequest.getItems());
            BigDecimal groceryItemsAmount = totalAmount.subtract(nonGroceryItemsAmount);

            if (requestedUser.getUserType().equals(EMPLOYEE)) {
                percentageDiscount = nonGroceryItemsAmount.multiply(BigDecimal.valueOf(0.30));
            } else if (requestedUser.getUserType().equals(AFFILIATE)) {
                percentageDiscount = nonGroceryItemsAmount.multiply(BigDecimal.valueOf(0.10));
            } else if (requestedUser.getUserType().equals(REGISTERED_CUSTOMER)){
                LocalDate joinDate = requestedUser.getJoiningDate();
                LocalDate currentDate = LocalDate.now();

                Period period = Period.between(joinDate, currentDate);

                if (period.getYears() > 2) {
                    percentageDiscount = nonGroceryItemsAmount.multiply(BigDecimal.valueOf(0.05));
                }
            }

            BigDecimal discountedAmount = nonGroceryItemsAmount.subtract(percentageDiscount);
            totalAmount = discountedAmount.add(groceryItemsAmount);
        }

        BigDecimal calBillBasedDiscount = totalAmount.divideToIntegralValue(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(5));

        BigDecimal totalPayableAmountAfterDiscounts = totalAmount.subtract(calBillBasedDiscount);
        // Total discount is the sum of percentage discount (only on non-grocery) and bill-based discount
        return totalPayableAmountAfterDiscounts;
    }

    private BigDecimal amountByItemType(List<RequestedItems> requestedItems) {
        List<RequestedItems> itemByType = requestedItems.stream()
                .filter(item -> !item.getCategory().equals("Grocery"))
                .collect(Collectors.toList());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (RequestedItems items : itemByType) {
            totalAmount = totalAmount.add(items.getPrice());
        }

        return totalAmount;
    }

    private BigDecimal currencyConversionRate(String sourceCurrency, String targetCurrency) {
        UriComponents uriBuilder = UriComponentsBuilder
                .fromUriString(API_BASE_URL)
                .pathSegment(API_KEY, "latest", sourceCurrency)
                .build();
        ResponseEntity<ConversionRateDto> response = restTemplate.getForEntity(uriBuilder.toUriString(), ConversionRateDto.class);
        
        BigDecimal targetedCurrency = BigDecimal.ZERO;
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            ConversionRateDto conversionRate = response.getBody();
            if (conversionRate.getResult().equals(SUCCESS)) {
                targetedCurrency = findTargetCurrency(targetCurrency, conversionRate);
            }
        }

        return targetedCurrency;
    }

    private BigDecimal findTargetCurrency(String targetCurrency, ConversionRateDto conversionRate) {
        return conversionRate.getConversion_rates().entrySet()
                .stream().filter(k -> k.getKey().equals(targetCurrency))
                .map(Map.Entry::getValue)
                .findFirst().orElse(BigDecimal.ZERO);
    }
}
