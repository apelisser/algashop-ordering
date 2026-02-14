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

    public abstract T getSortByPropertyOrDefault();
    public abstract Sort.Direction getSortDirectionOrDefault();

}
