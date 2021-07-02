package io.smallrye.converters.api;

public interface ConvertedType<S> {
    S getRaw();

    <T> T getAs(Class<T> klass);
}
