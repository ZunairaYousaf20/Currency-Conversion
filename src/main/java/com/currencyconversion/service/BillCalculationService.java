package com.currencyconversion.service;

import com.currencyconversion.request.BillCalculationRequest;
import com.currencyconversion.response.BillCalculationResponse;

public interface BillCalculationService {

    BillCalculationResponse calculatePayableAmount(BillCalculationRequest billCalculationRequest);
}
