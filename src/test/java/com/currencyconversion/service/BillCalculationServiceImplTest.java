package com.currencyconversion.service;

import com.currencyconversion.dto.ConversionRateDto;
import com.currencyconversion.request.BillCalculationRequest;
import com.currencyconversion.request.RequestedItems;
import com.currencyconversion.request.RequestedUser;
import com.currencyconversion.response.BillCalculationResponse;
import com.currencyconversion.service.Impl.BillCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillCalculationServiceImplTest {

    @InjectMocks
    private BillCalculationServiceImpl billCalculationService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(billCalculationService, "API_BASE_URL", "http://api.example.com");
        ReflectionTestUtils.setField(billCalculationService, "API_KEY", "dummyApiKey1232xe03");
    }

    @Test
    void testCalculatePayableAmount_employeeDiscount() {

        RequestedUser user = new RequestedUser();
        user.setUserType("EMPLOYEE");

        RequestedItems item1 = new RequestedItems(1L, "Electronics", new BigDecimal("100.00"), 1);
        RequestedItems item2 = new RequestedItems(2L, "Grocery", new BigDecimal("50.00"), 2);

        BillCalculationRequest request = new BillCalculationRequest();
        request.setUser(user);
        request.setItems(Arrays.asList(item1, item2));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("PKR");
        request.setTotalAmount(new BigDecimal("150.00"));

        ConversionRateDto conversionRateDto = new ConversionRateDto();
        conversionRateDto.setResult("success");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("PKR", new BigDecimal("277.70"));
        conversionRateDto.setConversion_rates(rates);

        when(restTemplate.getForEntity(anyString(), eq(ConversionRateDto.class)))
                .thenReturn(new ResponseEntity<>(conversionRateDto, HttpStatus.OK));

        BillCalculationResponse response = billCalculationService.calculatePayableAmount(request);
        assertNotNull(response);

        //discounted amount after applying all discount is 115.00 in source currency
        //converted into targeted current is equal to 31935.50000 in PKR
        assertEquals(new BigDecimal("31935.50000"), response.getTotalPayableAmount());
    }

    @Test
    void testCalculatePayableAmount_affiliateDiscount() {
        // Arrange
        RequestedUser user = new RequestedUser();
        user.setUserType("AFFILIATE");

        RequestedItems item1 = new RequestedItems(1L, "Electronics", new BigDecimal("200.00"), 1);
        RequestedItems item2 = new RequestedItems(2L, "Grocery", new BigDecimal("50.00"), 2);

        BillCalculationRequest request = new BillCalculationRequest();
        request.setUser(user);
        request.setItems(Arrays.asList(item1, item2));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setTotalAmount(new BigDecimal("250.00"));

        ConversionRateDto conversionRateDto = new ConversionRateDto();
        conversionRateDto.setResult("success");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.90"));
        conversionRateDto.setConversion_rates(rates);

        when(restTemplate.getForEntity(anyString(), eq(ConversionRateDto.class)))
                .thenReturn(new ResponseEntity<>(conversionRateDto, HttpStatus.OK));

        BillCalculationResponse response = billCalculationService.calculatePayableAmount(request);
        assertNotNull(response);

        //discounted amount after applying all discount is 220.000 in source currency
        //converted into targeted current is equal to 198.00000 in EUR
        assertEquals(new BigDecimal("198.00000"), response.getTotalPayableAmount());
    }

    @Test
    void testCalculatePayableAmount_userCustomerWithLessThanTwoYears() {
        // Arrange
        RequestedUser user = new RequestedUser();
        user.setUserType("CUSTOMER");
        user.setJoiningDate(LocalDate.now().minusYears(1));

        RequestedItems item1 = new RequestedItems(1L, "Electronics", new BigDecimal("150.00"), 1);
        RequestedItems item2 = new RequestedItems(2L, "Grocery", new BigDecimal("75.00"), 1);

        BillCalculationRequest request = new BillCalculationRequest();
        request.setUser(user);
        request.setItems(Arrays.asList(item1, item2));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("INR");
        request.setTotalAmount(new BigDecimal("225.00"));

        ConversionRateDto conversionRateDto = new ConversionRateDto();
        conversionRateDto.setResult("success");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("INR", new BigDecimal("74.50"));
        conversionRateDto.setConversion_rates(rates);

        when(restTemplate.getForEntity(anyString(), eq(ConversionRateDto.class)))
                .thenReturn(new ResponseEntity<>(conversionRateDto, HttpStatus.OK));

        BillCalculationResponse response = billCalculationService.calculatePayableAmount(request);
        assertNotNull(response);

        //discounted amount after applying all discount is 215.000 in source currency
        //converted into targeted current is equal to 16017.5000 in INR
        assertEquals(new BigDecimal("16017.5000"), response.getTotalPayableAmount());
    }
}
