package com.cloudera.flow;

public interface PayloadConverter<P> {
    boolean canConvert(Class<?> sourceClass);

    P convert(Object payload);
}
