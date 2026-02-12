package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.Specification;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;

import java.time.Year;

public class CustomerHasOrderedEnoughAtYearSpecification implements Specification<Customer> {

    private final Orders orders;
    private final long expectedOrderCount;

    public CustomerHasOrderedEnoughAtYearSpecification(Orders orders, long expectedOrderCount) {
        this.orders = orders;
        this.expectedOrderCount = expectedOrderCount;
    }

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return orders.salesQuantityByCustomerInYear(customer.id(), Year.now()) >= expectedOrderCount;
    }

}
