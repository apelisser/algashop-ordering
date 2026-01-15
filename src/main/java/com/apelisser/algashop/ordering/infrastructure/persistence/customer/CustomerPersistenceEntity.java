package com.apelisser.algashop.ordering.infrastructure.persistence.customer;

import com.apelisser.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class CustomerPersistenceEntity {

    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String document;
    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Integer loyaltyPoints;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "address.street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "address.number", column = @Column(name = "address_number")),
        @AttributeOverride(name = "address.complement", column = @Column(name = "address_complement")),
        @AttributeOverride(name = "address.neighborhood", column = @Column(name = "address_neighborhood")),
        @AttributeOverride(name = "address.city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "address.state", column = @Column(name = "address_state")),
        @AttributeOverride(name = "address.zipCode", column = @Column(name = "address_zipCode"))
    })
    private AddressEmbeddable address;

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @Version
    private Long version;

    @Builder
    public CustomerPersistenceEntity(UUID id, String firstName, String lastName, LocalDate birthDate, String email,
            String phone, String document, Boolean promotionNotificationsAllowed, Boolean archived,
            OffsetDateTime registeredAt, OffsetDateTime archivedAt, Integer loyaltyPoints, AddressEmbeddable address,
            UUID createdByUserId, OffsetDateTime createdAt, UUID lastModifiedByUserId, OffsetDateTime lastModifiedAt,
            Long version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.archived = archived;
        this.registeredAt = registeredAt;
        this.archivedAt = archivedAt;
        this.loyaltyPoints = loyaltyPoints;
        this.address = address;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.lastModifiedAt = lastModifiedAt;
        this.version = version;
    }

}
