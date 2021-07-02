package io.smallrye.converters.api;

@FunctionalInterface
public interface Converter<T> {
    T convert(String value);
}
