package com.apelisser.algashop.ordering.presentation;

import com.apelisser.algashop.ordering.application.customer.management.CustomerInput;
import com.apelisser.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.apelisser.algashop.ordering.application.customer.query.CustomerOutput;
import com.apelisser.algashop.ordering.application.customer.query.CustomerQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService customerQueryService;

    public CustomerController(CustomerManagementApplicationService customerManagementApplicationService,
        CustomerQueryService customerQueryService) {
        this.customerManagementApplicationService = customerManagementApplicationService;
        this.customerQueryService = customerQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input) {
        UUID customerId = customerManagementApplicationService.create(input);
        return customerQueryService.findById(customerId);
    }

}
