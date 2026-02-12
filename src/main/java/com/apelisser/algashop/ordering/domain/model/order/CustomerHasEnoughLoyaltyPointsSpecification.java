package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.Specification;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;

public class CustomerHasEnoughLoyaltyPointsSpecification implements Specification<Customer> {

    private final LoyaltyPoints expectedLoyaltyPoints;

    public CustomerHasEnoughLoyaltyPointsSpecification(LoyaltyPoints expectedLoyaltyPoints) {
        this.expectedLoyaltyPoints = expectedLoyaltyPoints;
    }

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return customer.loyaltyPoints().compareTo(expectedLoyaltyPoints) >= 0;
    }

}
