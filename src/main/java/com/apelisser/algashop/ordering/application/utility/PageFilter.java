package com.apelisser.algashop.ordering.application.utility;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PageFilter {

    private int size = 15;
    private int page = 0;

    public PageFilter() {
    }

    public PageFilter(int size, int page) {
        this.size = size;
        this.page = page;
    }

}
