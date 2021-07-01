package io.smallrye.converters.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.IntFunction;

public interface Converters extends Serializable {
    <T> Converter<T> getConverter(Class<T> asType);

    <T> Converter<Optional<T>> getOptionalConverter(Class<T> asType);

    <T> T convertValue(String value, Class<T> asType);

    <T> T convertValue(String value, Converter<T> converter);

    <T, C extends Collection<T>> C convertValues(String value, Class<T> asType, IntFunction<C> collectionFactory);

    <T, C extends Collection<T>> C convertValues(String value, Converter<T> converter, IntFunction<C> collectionFactory);
}
