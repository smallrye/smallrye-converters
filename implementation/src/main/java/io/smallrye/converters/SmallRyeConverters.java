package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

import io.smallrye.converters.api.Converter;

public class SmallRyeConverters implements io.smallrye.converters.api.Converters {
    private final Map<Type, Converter<?>> converters;
    private final Map<Type, Converter<Optional<?>>> optionalConverters = new ConcurrentHashMap<>();

    SmallRyeConverters() {
        converters = new ConcurrentHashMap<>(Converters.ALL_CONVERTERS);
    }

    SmallRyeConverters(final SmallRyeConvertersBuilder smallRyeConvertersBuilder) {
        this();
        smallRyeConvertersBuilder.getConverters()
                .forEach((type, converter) -> this.converters.put(type, converter.getConverter()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> getConverter(Class<T> asType) {
        final Converter<?> exactConverter = converters.get(asType);
        if (exactConverter != null) {
            return (Converter<T>) exactConverter;
        }
        if (asType.isPrimitive()) {
            return (Converter<T>) getConverter(Converters.wrapPrimitiveType(asType));
        }
        if (asType.isArray()) {
            final Converter<?> conv = getConverter(asType.getComponentType());
            return conv == null ? null : Converters.newArrayConverter(conv, asType);
        }
        return (Converter<T>) converters.computeIfAbsent(asType, clazz -> ImplicitConverters.getConverter((Class<?>) clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<Optional<T>> getOptionalConverter(Class<T> asType) {
        return optionalConverters.computeIfAbsent(asType,
                clazz -> Converters.newOptionalConverter(getConverter((Class) clazz)));
    }

    @Override
    public <T> T convertValue(final String value, final Class<T> asType) {
        return getConverter(asType).convert(value);
    }

    @Override
    public <T> T convertValue(final String value, final Converter<T> converter) {
        return converter.convert(value);
    }

    @Override
    public <T, C extends Collection<T>> C convertValues(final String value, final Class<T> asType,
            final IntFunction<C> collectionFactory) {
        return convertValues(value, getConverter(asType), collectionFactory);
    }

    @Override
    public <T, C extends Collection<T>> C convertValues(final String value, final Converter<T> converter,
            final IntFunction<C> collectionFactory) {
        return convertValue(value, Converters.newCollectionConverter(converter, collectionFactory));
    }
}
