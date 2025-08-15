package com.domain.account.usecase;

public interface Usecase<R, K> {

    R execute(K request);
}
