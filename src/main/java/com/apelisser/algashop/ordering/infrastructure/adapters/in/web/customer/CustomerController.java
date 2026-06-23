package com.apelisser.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerInput;
import com.apelisser.algashop.ordering.core.application.customer.CustomerManagementApplicationService;
import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerUpdateInput;
import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerFilter;
import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.apelisser.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerManagementApplicationService forManagingCustomers;
    private final ForQueryingCustomers customerQueryService;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    public CustomerController(CustomerManagementApplicationService forManagingCustomers,
            ForQueryingCustomers customerQueryService, ForQueryingShoppingCarts forQueryingShoppingCarts) {
        this.forManagingCustomers = forManagingCustomers;
        this.customerQueryService = customerQueryService;
        this.forQueryingShoppingCarts = forQueryingShoppingCarts;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input, HttpServletResponse response) {
        UUID customerId = forManagingCustomers.create(input);

        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(
            MvcUriComponentsBuilder.on(CustomerController.class).findById(customerId));
        response.addHeader("Location", builder.toUriString());

        return customerQueryService.findById(customerId);
    }

    @GetMapping
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter) {
        return PageModel.of(customerQueryService.filter(customerFilter));
    }

    @GetMapping("/{customerId}")
    public CustomerOutput findById(@PathVariable UUID customerId) {
        return customerQueryService.findById(customerId);
    }

    @GetMapping("/{customerId}/shopping-cart")
    public ShoppingCartOutput findShoppingCartByCustomerId(@PathVariable UUID customerId) {
        return forQueryingShoppingCarts.findByCustomerId(customerId);
    }

    @PutMapping("/{customerId}")
    public CustomerOutput update(@PathVariable UUID customerId, @RequestBody @Valid CustomerUpdateInput input) {
        forManagingCustomers.update(customerId, input);
        return customerQueryService.findById(customerId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID customerId) {
        forManagingCustomers.archive(customerId);
    }

}
