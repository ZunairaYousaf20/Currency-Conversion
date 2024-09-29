package com.currencyconversion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "user_type")
    private String userType;

    private String password;
    private String role;

    @Column(name = "joining_date")
    private LocalDate joiningDate;
}