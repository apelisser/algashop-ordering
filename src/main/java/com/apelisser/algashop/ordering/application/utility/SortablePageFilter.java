package com.apelisser.algashop.ordering.application.utility;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class SortablePageFilter<T> extends PageFilter {

    private T sortByProperty;
    private Sort.Direction sortDirection;

    public SortablePageFilter() {
    }

    public SortablePageFilter(int page, int size) {
        super(page, size);
    }

    public SortablePageFilter(T sortByProperty, Sort.Direction sortDirection) {
        this.sortByProperty = sortByProperty;
        this.sortDirection = sortDirection;
    }

    public SortablePageFilter(int size, int page, T sortByProperty, Sort.Direction sortDirection) {
        super(size, page);
        this.sortByProperty = sortByProperty;
        this.sortDirection = sortDirection;
    }

    public abstract T getSortByPropertyOrDefault();
    public abstract Sort.Direction getSortDirectionOrDefault();

}
