package com.apelisser.algashop.ordering.infrastructure.persistence.disassembler;

import com.apelisser.algashop.ordering.domain.model.entity.Customer;
import com.apelisser.algashop.ordering.domain.model.valueobject.Address;
import com.apelisser.algashop.ordering.domain.model.valueobject.BirthDate;
import com.apelisser.algashop.ordering.domain.model.valueobject.Document;
import com.apelisser.algashop.ordering.domain.model.valueobject.Email;
import com.apelisser.algashop.ordering.domain.model.valueobject.FullName;
import com.apelisser.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.model.valueobject.Phone;
import com.apelisser.algashop.ordering.domain.model.valueobject.ZipCode;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(CustomerPersistenceEntity persistenceEntity) {
        return Customer.existing()
            .id(new CustomerId(persistenceEntity.getId()))
            .fullName(new FullName(persistenceEntity.getFirstName(),  persistenceEntity.getLastName()))
            .birthDate(new BirthDate(persistenceEntity.getBirthDate()))
            .email(new Email(persistenceEntity.getEmail()))
            .phone(new Phone(persistenceEntity.getPhone()))
            .document(new Document(persistenceEntity.getDocument()))
            .promotionNotificationsAllowed(persistenceEntity.getPromotionNotificationsAllowed())
            .archived(persistenceEntity.getArchived())
            .registeredAt(persistenceEntity.getRegisteredAt())
            .archivedAt(persistenceEntity.getArchivedAt())
            .loyaltyPoints(new LoyaltyPoints(persistenceEntity.getLoyaltyPoints()))
            .address(this.address(persistenceEntity.getAddress()))
            .version(persistenceEntity.getVersion())
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

}
