package com.domain.account.usecase.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResult<T>(List<T> content,
                            Long totalElements,
                            Integer totalPages) { }
