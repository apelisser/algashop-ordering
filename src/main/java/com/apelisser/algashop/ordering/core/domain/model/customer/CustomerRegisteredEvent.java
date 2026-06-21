package com.apelisser.algashop.ordering.core.domain.model.customer;

import com.apelisser.algashop.ordering.core.domain.model.commons.Email;
import com.apelisser.algashop.ordering.core.domain.model.commons.FullName;

import java.time.OffsetDateTime;

public record CustomerRegisteredEvent(
    CustomerId customerId,
    OffsetDateTime registeredAt,
    FullName fullName,
    Email email
) {

}
