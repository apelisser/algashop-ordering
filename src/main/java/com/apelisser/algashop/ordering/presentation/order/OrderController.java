package com.apelisser.algashop.ordering.presentation.order;

import com.apelisser.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.apelisser.algashop.ordering.application.checkout.BuyNowInput;
import com.apelisser.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.apelisser.algashop.ordering.application.checkout.CheckoutInput;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderFilter;
import com.apelisser.algashop.ordering.application.order.query.OrderQueryService;
import com.apelisser.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
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

    private final OrderQueryService orderQueryService;
    private final BuyNowApplicationService buyNowApplicationService;
    private final CheckoutApplicationService checkoutApplicationService;

    public OrderController(OrderQueryService orderQueryService,
            BuyNowApplicationService buyNowApplicationService, CheckoutApplicationService checkoutApplicationService) {
        this.orderQueryService = orderQueryService;
        this.buyNowApplicationService = buyNowApplicationService;
        this.checkoutApplicationService = checkoutApplicationService;
    }

    @GetMapping("/{orderId}")
    public OrderDetailOutput findById(@PathVariable String orderId) {
        return orderQueryService.findById(orderId);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(orderQueryService.filter(filter));
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
        return orderQueryService.findById(orderId);
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
        return orderQueryService.findById(orderId);
    }

}
