package com.practice.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private int customerId;

    private String name;

    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "create_dt")
    private LocalDate createDt;

}