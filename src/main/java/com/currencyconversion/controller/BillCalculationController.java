package com.currencyconversion.controller;

import com.currencyconversion.request.BillCalculationRequest;
import com.currencyconversion.response.BillCalculationResponse;
import com.currencyconversion.service.BillCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class BillCalculationController {

    @Autowired
    private BillCalculationService billCalculationService;

    @PostMapping("/api/calculate")
    public ResponseEntity<BillCalculationResponse> billCalculation(@RequestBody BillCalculationRequest billCalculationRequest) {
        return ResponseEntity.ok(billCalculationService.calculatePayableAmount(billCalculationRequest));
    }
}
