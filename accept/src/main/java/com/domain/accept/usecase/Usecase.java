package com.domain.accept.usecase;

public interface Usecase<R, K> {

    R execute(K request);
}
