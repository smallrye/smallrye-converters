package io.smallrye.converters.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.IntFunction;

import org.eclipse.microprofile.config.spi.Converter;

import io.smallrye.converters.Converters;
import io.smallrye.converters.SmallRyeConverters;

public class SmallRyeConfigConverters implements Serializable {
    private SmallRyeConverters converters;

    SmallRyeConfigConverters(final SmallRyeConfigConvertersBuilder builder) {
        this.converters = builder.getConvertersBuilder().build();
    }

    public <T> Converter<T> getConverter(final Class<T> asType) {
        return (Converter<T>) value -> converters.getConverter(asType).convert(value);
    }

    public <T> Converter<Optional<T>> getOptionalConverter(final Class<T> asType) {
        return (Converter<Optional<T>>) value -> converters.getOptionalConverter(asType).convert(value);
    }

    public <T> Converter<Optional<T>> newOptionalConverter(Converter<? extends T> delegateConverter) {
        return Converters.<T> newOptionalConverter(delegateConverter::convert)::convert;
    }

    public <T, C extends Collection<T>> Converter<C> newCollectionConverter(
            Converter<? extends T> itemConverter,
            IntFunction<C> collectionFactory) {
        return Converters.newCollectionConverter(itemConverter::convert, collectionFactory)::convert;
    }
}
