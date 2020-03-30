package io.smallrye.converters.api;

import java.io.Serializable;
import java.util.Optional;

public interface Converters extends Serializable {
    <T> Converter<T> getConverter(Class<T> asType);

    <T> Converter<Optional<T>> getOptionalConverter(Class<T> asType);

    <T> T convertValue(String value, Class<T> asType);

    <T> T convertValue(String value, Converter<T> converter);
}
