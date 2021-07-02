package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.smallrye.converters.api.ConvertedType;
import io.smallrye.converters.api.Converter;
import io.smallrye.converters.api.Converters;
import io.smallrye.converters.api.ConvertersProvider;
import io.smallrye.converters.api.OutputConverter;

public class ConvertedTypeImpl<S> implements ConvertedType<S> {
    private final S value;
    private final Map<Type, OutputConverter<S, ?>> converters;

    public ConvertedTypeImpl(final S value, final Map<Type, OutputConverter<S, ?>> converters) {
        assert value != null;

        this.value = value;
        this.converters = converters;
    }

    public Object getValue() {
        return value;
    }

    public String getAsString() {
        return value.toString();
    }

    public Number getAsNumber() {
        return getAs(Double.class);
    }

    public Boolean getAsBoolean() {
        return getAs(Boolean.class);
    }

    @Override
    public S getRaw() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAs(Class<T> klass) {
        if (klass.isInstance(value)) {
            return (T) value;
        }

        OutputConverter<S, T> converter = (OutputConverter<S, T>) converters.get(klass);
        if (converter == null) {
            throw ConverterMessages.msg.noRegisteredConverter(klass);
        }

        return converter.convert((this));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAsList(Class<T> klass) {
        if (List.class.isAssignableFrom(value.getClass())) {
            return (List<T>) ((List) value).stream()
                    .map(o -> ConvertedTypeImpl.of(o).getAs(klass))
                    .collect(Collectors.toList());
        }

        if (value instanceof String) {
            return (List<T>) ConvertersUtils.newCollectionConverter(
                    (Converter<Object>) value -> ConvertedTypeImpl.of(value).getAs(klass), ArrayList::new)
                    .convert((String) value);
        }

        throw new IllegalStateException();
    }

    public <K, V> Map<K, V> getAsMap(Class<K> key, Class<V> value) {
        if (Map.class.isAssignableFrom(this.value.getClass())) {
            return ((Set<Map.Entry>) ((Map) this.value).entrySet())
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(ConvertedTypeImpl.of(entry.getKey()).getAs(key),
                            ConvertedTypeImpl.of(entry.getValue()).getAs(value)))
                    .collect(Collectors.toMap(entry -> (K) entry.getKey(),
                            entry -> (V) entry.getValue()));
        }

        throw new IllegalStateException();
    }

    static <S> ConvertedTypeImpl<S> of(final S rawValue) {
        return of(rawValue, ConvertersProvider.getConverters());
    }

    static <S> ConvertedTypeImpl<S> of(final S rawValue, final Converters converters) {
        throw new UnsupportedOperationException();
    }
}
