package com.currencyconversion.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RequestedUser {

    private Long userId;
    private String userName;
    private String userType;
    private LocalDate joiningDate;
}
