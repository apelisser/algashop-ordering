package com.apelisser.algashop.ordering.infrastructure.adapters.in.web.order;

import com.apelisser.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.apelisser.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.apelisser.algashop.ordering.core.application.checkout.CheckoutApplicationService;
import com.apelisser.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.apelisser.algashop.ordering.core.ports.in.order.OrderDetailOutput;
import com.apelisser.algashop.ordering.core.ports.in.order.OrderFilter;
import com.apelisser.algashop.ordering.core.ports.in.order.OrderSummaryOutput;
import com.apelisser.algashop.ordering.presentation.PageModel;
import com.apelisser.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final ForQueryingOrders forQueryingOrders;
    private final BuyNowApplicationService buyNowApplicationService;
    private final CheckoutApplicationService checkoutApplicationService;

    public OrderController(ForQueryingOrders forQueryingOrders,
            BuyNowApplicationService buyNowApplicationService, CheckoutApplicationService checkoutApplicationService) {
        this.forQueryingOrders = forQueryingOrders;
        this.buyNowApplicationService = buyNowApplicationService;
        this.checkoutApplicationService = checkoutApplicationService;
    }

    @GetMapping("/{orderId}")
    public OrderDetailOutput findById(@PathVariable String orderId) {
        return forQueryingOrders.findById(orderId);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(forQueryingOrders.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput buyNow(@RequestBody @Valid BuyNowInput input) {
        String orderId;
        try {
            orderId = buyNowApplicationService.buyNow(input);
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return forQueryingOrders.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput checkout(@RequestBody @Valid CheckoutInput input) {
        String orderId;
        try {
            orderId = checkoutApplicationService.checkout(input);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return forQueryingOrders.findById(orderId);
    }

}
