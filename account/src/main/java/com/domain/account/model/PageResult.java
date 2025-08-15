package com.domain.account.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PageResult<T> {

    private final List<T> content;

    private final Long totalElements;

    private final Integer totalPages;
}
