package com.domain.account.usecase.request;

import java.util.List;

public record PageResult<T>(List<T> content, Long totalElements, Integer totalPages) {

}
