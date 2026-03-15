package com.apelisser.algashop.ordering.contract.base;

import com.apelisser.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.apelisser.algashop.ordering.application.checkout.BuyNowInput;
import com.apelisser.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.apelisser.algashop.ordering.application.checkout.CheckoutInput;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutputTestDataBuilder;
import com.apelisser.algashop.ordering.application.order.query.OrderFilter;
import com.apelisser.algashop.ordering.application.order.query.OrderQueryService;
import com.apelisser.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderSummaryOutputTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.presentation.order.OrderController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = OrderController.class)
public class OrderBase {

    @Autowired
    WebApplicationContext context;

    @MockitoBean
    OrderQueryService orderQueryService;

    @MockitoBean
    BuyNowApplicationService buyNowApplicationService;

    @MockitoBean
    CheckoutApplicationService checkoutApplicationService;

    public static final String VALID_ORDER_ID = "01226N0640J7Q";
    public static final String INVALID_ORDER_ID = "01226N0693HDH";

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidOrderFindById();
        mockInvalidOrderFindById();
        mockCreateOrderBuyNow();
        mockCreateOrderCheckout();
        mockFilterCategories();
    }

    private void mockCreateOrderCheckout() {
        Mockito.when(checkoutApplicationService.checkout(Mockito.any(CheckoutInput.class)))
            .thenReturn(VALID_ORDER_ID);
    }

    private void mockCreateOrderBuyNow() {
        Mockito.when(buyNowApplicationService.buyNow(Mockito.any(BuyNowInput.class)))
            .thenReturn(VALID_ORDER_ID);
    }

    private void mockInvalidOrderFindById() {
        Mockito.when(orderQueryService.findById(INVALID_ORDER_ID))
            .thenThrow(new OrderNotFoundException());
    }

    private void mockValidOrderFindById() {
        Mockito.when(orderQueryService.findById(VALID_ORDER_ID))
            .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(VALID_ORDER_ID).build());
    }

    private void mockFilterCategories() {
        Mockito.when(orderQueryService.filter(Mockito.any(OrderFilter.class)))
            .then(answer -> {
                OrderFilter filter = answer.getArgument(0);

                long totalElements = 2;
                PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
                List<OrderSummaryOutput> content = List.of(
                    OrderSummaryOutputTestDataBuilder.placedOrder().id("0PRC7KBEZ3CD3").build(),
                    OrderSummaryOutputTestDataBuilder.placedOrder().id("0PRC7NDPZ3EVJ").build()
                );

                return new PageImpl<>(content, pageRequest, totalElements);
            });
    }

}
