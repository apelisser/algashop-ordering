package com.apelisser.algashop.ordering.application.customer.query;

import com.apelisser.algashop.ordering.application.utility.SortablePageFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CustomerFilter extends SortablePageFilter<CustomerFilter.SortType> {

    private String email;
    private String firstName;

    public CustomerFilter() {
    }

    public CustomerFilter(int page, int size) {
        super(page, size);
    }

    public CustomerFilter(int page, int size, String email, String firstName) {
        super(page, size);
        this.email = email;
        this.firstName = firstName;
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.REGISTERED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    public enum SortType {
        REGISTERED_AT("registeredAt"),
        FIRST_NAME("firstName");

        private final String propertyName;

        SortType(String propertyName) {
            this.propertyName = propertyName;
        }
    }

}
