package com.apelisser.algashop.ordering.presentation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class PageModel<T> {

    private int number;
    private int size;
    private int totalPages;
    private long totalElements;

    @Builder.Default
    private List<T> content = new ArrayList<>();

    public static <T> PageModel<T> of(Page<T> page) {
        return PageModel.<T>builder()
            .number(page.getNumber())
            .size(page.getSize())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .content(page.getContent())
            .build();
    }

}
