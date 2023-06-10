package com.practice.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.practice.accounts.config.AccountsServiceConfig;
import com.practice.accounts.model.*;
import com.practice.accounts.repository.AccountsRepository;
import com.practice.accounts.service.client.CardsFeignClient;
import com.practice.accounts.service.client.LoansFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AccountsController {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    AccountsServiceConfig accountsConfig;

    @Autowired
    CardsFeignClient cardsFeignClient;

    @Autowired
    LoansFeignClient loansFeignClient;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        if (accounts != null) {
            return accounts;
        } else {
            return null;
        }
    }

    @GetMapping("/accounts/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
                accountsConfig.getMailDetails() ,accountsConfig.getActiveBranches());
        String jsonString = ow.writeValueAsString(properties);
        return jsonString;
    }

    @PostMapping("/myCustomerDetails")
    //@CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallback")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallback")
    public CustomerDetails getCustomerDetails(@RequestHeader("myBankApp-correlation-id") String correlationId, @RequestBody Customer customer){
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accountsRepository.findByCustomerId(customer.getCustomerId()));
        customerDetails.setCards(cardsFeignClient.getCardDetails(correlationId, customer));
        customerDetails.setLoans(loansFeignClient.getLoansDetails(correlationId, customer));
        return customerDetails;
    }

    private CustomerDetails myCustomerDetailsFallback(Customer customer, Throwable t){
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accountsRepository.findByCustomerId(customer.getCustomerId()));
//        try{
//            customerDetails.setCards(cardsFeignClient.getCardDetails(customer));
//        }catch (Exception e){
//            try{
//                customerDetails.setLoans(loansFeignClient.getLoansDetails(customer));
//                return customerDetails;
//            }catch (Exception e1){
//                log.info("Could not add Loans info because: {}", e1);
//                return customerDetails;
//            }
//        }
        //customerDetails.setCards(cardsFeignClient.getCardDetails(customer));
        //customerDetails.setLoans(loansFeignClient.getLoansDetails(customer));
        return customerDetails;
    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome to My Bank App";
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi, Welcome to My Bank App";
    }
}