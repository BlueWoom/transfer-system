package com.domain.account.port.query;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;

public record AccountPageQuery(int pageNumber, int pageSize) {

    public AccountPageQuery {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new AccountDomainException(AccountDomainErrorCode.INVALID_REQUEST, "Page number must be non-negative and page size must be positive.");
        }

    }
}