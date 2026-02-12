package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.Specification;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;

import java.time.Year;

public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {

    private final Orders orders;

    private final int minPointsForFreeShippingRule1;
    private final long salesQuantityForFreeShippingRule1;

    private final int minPointsForFreeShippingRule2;

    public CustomerHaveFreeShippingSpecification(Orders orders, int minPointsForFreeShippingRule1,
            long salesQuantityForFreeShippingRule1, int minPointsForFreeShippingRule2) {
        this.orders = orders;
        this.minPointsForFreeShippingRule1 = minPointsForFreeShippingRule1;
        this.salesQuantityForFreeShippingRule1 = salesQuantityForFreeShippingRule1;
        this.minPointsForFreeShippingRule2 = minPointsForFreeShippingRule2;
    }

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return customer.loyaltyPoints().compareTo(new LoyaltyPoints(minPointsForFreeShippingRule1)) >= 0
            && orders.salesQuantityByCustomerInYear(customer.id(), Year.now()) >= salesQuantityForFreeShippingRule1
            || customer.loyaltyPoints().compareTo(new LoyaltyPoints(minPointsForFreeShippingRule2)) >= 0;
    }

}
