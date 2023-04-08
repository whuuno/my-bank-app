package com.practice.cards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
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