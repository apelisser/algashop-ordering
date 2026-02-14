package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.utility.PageFilter;
import org.springframework.data.domain.Page;

public interface OrderQueryService {

    OrderDetailOutput findById(String id);

    Page<OrderSummaryOutput> filter(PageFilter filter);

}
