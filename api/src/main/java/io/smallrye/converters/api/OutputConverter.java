package io.smallrye.converters.api;

@FunctionalInterface
public interface OutputConverter<S, T> {
    T convert(ConvertedType<S> value);
}
