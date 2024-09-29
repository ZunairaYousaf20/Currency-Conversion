package com.currencyconversion.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String name;
    private String email;
    private String phoneNo;
    private String userType;
    private LocalDate joiningDate;
}
