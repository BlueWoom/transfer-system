package com.domain.transfer.usecase;

public interface Usecase<R, K> {

    R execute(K request);
}
