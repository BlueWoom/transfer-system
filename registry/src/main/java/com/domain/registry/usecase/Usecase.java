package com.domain.registry.usecase;

public interface Usecase<R, K> {

    R execute(K request);
}
