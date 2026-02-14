package com.apelisser.algashop.ordering.infrastructure.persistence.order;

import com.apelisser.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderFilter;
import com.apelisser.algashop.ordering.application.order.query.OrderQueryService;
import com.apelisser.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.apelisser.algashop.ordering.application.utility.Mapper;
import com.apelisser.algashop.ordering.application.utility.PageFilter;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;
    private final EntityManager entityManager;


    public OrderQueryServiceImpl(OrderPersistenceEntityRepository repository, Mapper mapper, EntityManager entityManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public OrderDetailOutput findById(String id) {
        OrderPersistenceEntity entity = repository.findById(new OrderId(id).value().toLong())
            .orElseThrow(OrderNotFoundException::new);

        return mapper.convert(entity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(0), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(OrderFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);

        Expression<Long> count = builder.count(root);
        criteriaQuery.select(count);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        return query.getSingleResult();
    }

    private Page<OrderSummaryOutput> filterQuery(PageFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = builder.createQuery(OrderSummaryOutput.class);

        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);

        Path<Object> customer = root.get("customer");

        criteriaQuery.select(
            builder.construct(OrderSummaryOutput.class,
                root.get("id"),
                builder.construct(CustomerMinimalOutput.class,
                    customer.get("id"),
                    customer.get("firstName"),
                    customer.get("lastName"),
                    customer.get("email"),
                    customer.get("document"),
                    customer.get("phone")),
                root.get("totalItems"),
                root.get("totalAmount"),
                root.get("placedAt"),
                root.get("paidAt"),
                root.get("canceledAt"),
                root.get("readyAt"),
                root.get("status"),
                root.get("paymentMethod"))
        );

        TypedQuery<OrderSummaryOutput> query = entityManager.createQuery(criteriaQuery);

        query.setFirstResult(filter.getPage() * filter.getSize());
        query.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResults);
    }

}
