package io.smallrye.converters.api;

@FunctionalInterface
public interface InputConverter<S, T> {
    T convert(S value);
}
