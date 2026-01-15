package com.apelisser.algashop.ordering.infrastructure.persistence.disassembler;

import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.domain.model.commons.Document;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.domain.model.order.Billing;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderItem;
import com.apelisser.algashop.ordering.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.order.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.order.Recipient;
import com.apelisser.algashop.ordering.domain.model.order.Shipping;
import com.apelisser.algashop.ordering.domain.model.product.ProductName;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderItemId;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
            .id(new OrderId(persistenceEntity.getId()))
            .customerId(new CustomerId(persistenceEntity.getCustomerId()))
            .totalAmount(new Money(persistenceEntity.getTotalAmount()))
            .totalItems(new Quantity(persistenceEntity.getTotalItems()))
            .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
            .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
            .placedAt(persistenceEntity.getPlacedAt())
            .paidAt(persistenceEntity.getPaidAt())
            .canceledAt(persistenceEntity.getCanceledAt())
            .readyAt(persistenceEntity.getReadyAt())
            .version(persistenceEntity.getVersion())
            .items(this.items(persistenceEntity.getItems()))
            .billing(this.billing(persistenceEntity.getBilling()))
            .shipping(this.shipping(persistenceEntity.getShipping()))
            .build();
    }

    private Set<OrderItem> items(Set<OrderItemPersistenceEntity> itemsPersistence) {
        if (itemsPersistence == null || itemsPersistence.isEmpty()) {
            return new HashSet<>();
        }

        return itemsPersistence.stream()
            .map(item -> OrderItem.existing()
                .id(new OrderItemId(item.getId()))
                .orderId(new OrderId(item.getOrder().getId()))
                .productId(new ProductId(item.getProductId()))
                .productName(new ProductName(item.getProductName()))
                .price(new Money(item.getPrice()))
                .quantity(new Quantity(item.getQuantity()))
                .totalAmount(new Money(item.getTotalAmount()))
                .build())
            .collect(Collectors.toSet());
    }

    private Billing billing(BillingEmbeddable billingEmbeddable) {
        if (billingEmbeddable == null) return null;

        return Billing.builder()
            .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
            .document(new Document(billingEmbeddable.getDocument()))
            .phone(new Phone(billingEmbeddable.getPhone()))
            .email(new Email(billingEmbeddable.getEmail()))
            .address(this.address(billingEmbeddable.getAddress()))
            .build();
    }

    private Shipping shipping(ShippingEmbeddable shippingEmbeddable) {
        if (shippingEmbeddable == null) return null;

        return Shipping.builder()
            .cost(new Money(shippingEmbeddable.getCost()))
            .expectedDate(shippingEmbeddable.getExpectedDate())
            .recipient(this.recipient(shippingEmbeddable.getRecipient()))
            .address(this.address(shippingEmbeddable.getAddress()))
            .build();
    }

    private Address address(AddressEmbeddable addressEmbeddable) {
        if (addressEmbeddable == null) return null;

        return Address.builder()
            .street(addressEmbeddable.getStreet())
            .complement(addressEmbeddable.getComplement())
            .neighborhood(addressEmbeddable.getNeighborhood())
            .number(addressEmbeddable.getNumber())
            .city(addressEmbeddable.getCity())
            .state(addressEmbeddable.getState())
            .zipCode(new ZipCode(addressEmbeddable.getZipCode()))
            .build();
    }

    private Recipient recipient(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) return null;

        return Recipient.builder()
            .fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
            .document(new Document(recipientEmbeddable.getDocument()))
            .phone(new Phone(recipientEmbeddable.getPhone()))
            .build();
    }

}
