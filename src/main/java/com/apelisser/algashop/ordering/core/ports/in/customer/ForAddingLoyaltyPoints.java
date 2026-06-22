package com.apelisser.algashop.ordering.core.ports.in.customer;

import java.util.UUID;

public interface ForAddingLoyaltyPoints {

    void addLoyaltyPoints(UUID customerId, String orderId);

}
