package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.apelisser.algashop.ordering.domain.model.DomainException;

import java.io.Serial;

public class OrderNotFoundException extends DomainEntityNotFoundException {

    @Serial
    private static final long serialVersionUID = 8580594876801659311L;

}
