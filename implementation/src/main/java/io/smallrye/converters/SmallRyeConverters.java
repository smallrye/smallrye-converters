package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

import io.smallrye.converters.api.ConvertedType;
import io.smallrye.converters.api.Converter;
import io.smallrye.converters.api.InputConverter;
import io.smallrye.converters.api.OutputConverter;

public class SmallRyeConverters implements io.smallrye.converters.api.Converters {
    private final Map<Type, Converter<?>> converters;
    private final Map<Type, Converter<Optional<?>>> optionalConverters = new ConcurrentHashMap<>();

    private final Map<Type, InputConverter<?, ?>> inputConverters = new HashMap<>();
    private final Map<Type, OutputConverter<?, ?>> outputConverters = new HashMap<>();

    SmallRyeConverters() {
        converters = new ConcurrentHashMap<>(ConvertersUtils.ALL_CONVERTERS);
    }

    SmallRyeConverters(final SmallRyeConvertersBuilder builder) {
        this();
        builder.getConverters()
                .forEach((type, converter) -> this.converters.put(type, converter.getConverter()));

        // TODO - add Priority
        inputConverters.putAll(builder.getInputConverters());
        outputConverters.putAll(builder.getOutputConverters());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> getConverter(Class<T> asType) {
        final Converter<?> exactConverter = converters.get(asType);
        if (exactConverter != null) {
            return (Converter<T>) exactConverter;
        }
        if (asType.isPrimitive()) {
            return (Converter<T>) getConverter(ConvertersUtils.wrapPrimitiveType(asType));
        }
        if (asType.isArray()) {
            final Converter<?> conv = getConverter(asType.getComponentType());
            return conv == null ? null : ConvertersUtils.newArrayConverter(conv, asType);
        }
        return (Converter<T>) converters.computeIfAbsent(asType, clazz -> ImplicitConverters.getConverter((Class<?>) clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<Optional<T>> getOptionalConverter(Class<T> asType) {
        return optionalConverters.computeIfAbsent(asType,
                clazz -> ConvertersUtils.newOptionalConverter(getConverter((Class) clazz)));
    }

    @Override
    public <T> T convert(final String value, final Class<T> asType) {
        return getConverter(asType).convert(value);
    }

    @Override
    public <T> T convert(final String value, final Converter<T> converter) {
        return converter.convert(value);
    }

    @Override
    public <T, C extends Collection<T>> C convert(final String value, final Class<T> asType,
            final IntFunction<C> collectionFactory) {
        return convert(value, getConverter(asType), collectionFactory);
    }

    @Override
    public <T, C extends Collection<T>> C convert(final String value, final Converter<T> converter,
            final IntFunction<C> collectionFactory) {
        return convert(value, ConvertersUtils.newCollectionConverter(converter, collectionFactory));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> ConvertedType<T> from(final S value) {
        InputConverter<S, T> inputConverter = (InputConverter<S, T>) getInputConverter(value.getClass());
        T input = inputConverter.convert(value);
        Map<Type, ? extends OutputConverter<?, ?>> outputConverters = getOutputConverters(value.getClass());
        return new ConvertedTypeImpl<T>(input, (Map<Type, OutputConverter<T, ?>>) outputConverters);
    }

    @SuppressWarnings("unchecked")
    private <S, T> InputConverter<S, T> getInputConverter(final Class<S> asType) {
        InputConverter<S, ?> exactConverter = (InputConverter<S, ?>) inputConverters.get(asType);
        if (exactConverter != null) {
            return (InputConverter<S, T>) exactConverter;
        }

        // passthrough
        return value -> (T) value;
    }

    @SuppressWarnings("unchecked")
    private <S> Map<Type, OutputConverter<S, ?>> getOutputConverters(final Class<S> asType) {
        Map<Type, OutputConverter<S, ?>> converters = new HashMap<>();
        for (Map.Entry<Type, OutputConverter<?, ?>> entry : outputConverters.entrySet()) {
            // TODO - Filter based on S,T of OutputConverter
            converters.put(entry.getKey(), (OutputConverter<S, ?>) entry.getValue());
        }
        return converters;
    }
}
