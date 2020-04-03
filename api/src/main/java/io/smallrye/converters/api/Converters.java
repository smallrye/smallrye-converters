package io.smallrye.converters.api;

import java.util.Collection;
import java.util.Optional;
import java.util.function.IntFunction;

public interface Converters {
    <T> Converter<T> getConverter(Class<T> asType);

    <T> Converter<Optional<T>> getOptionalConverter(Class<T> asType);

    <T> T convert(String value, Class<T> asType);

    <T> T convert(String value, Converter<T> converter);

    <T, C extends Collection<T>> C convert(String value, Class<T> asType, IntFunction<C> collectionFactory);

    <T, C extends Collection<T>> C convert(String value, Converter<T> converter, IntFunction<C> collectionFactory);

    <S, T> ConvertedType<T> from(S value);
}
