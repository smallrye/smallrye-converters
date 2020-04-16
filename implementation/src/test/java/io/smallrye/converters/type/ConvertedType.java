package io.smallrye.converters.type;

import java.io.StringReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;

import io.smallrye.converters.SmallRyeConvertersBuilder;
import io.smallrye.converters.api.Converter;
import io.smallrye.converters.api.Converters;

public class ConvertedType {
    private final Object rawValue;
    private final Converters converters;

    public ConvertedType(final Object rawValue, final Converters converters) {
        this.rawValue = rawValue;
        this.converters = converters;
    }

    public Object getRawValue() {
        return rawValue;
    }

    public String getAsString() {
        return rawValue.toString();
    }

    public Number getAsNumber() {
        return getAs(Double.class);
    }

    public Boolean getAsBoolean() {
        return getAs(Boolean.class);
    }

    public <T> T getAs(Class<T> klass) {
        if (klass.isInstance(rawValue)) {
            return (T) rawValue;
        }

        final Converter<T> converter = converters.getConverter(klass);
        return converter != null ? converter.convert(rawValue.toString()) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAsList(Class<T> klass) {
        if (List.class.isAssignableFrom(rawValue.getClass())) {
            return (List<T>) ((List) rawValue).stream()
                    .map(o -> ConvertedType.of(o).getAs(klass))
                    .collect(Collectors.toList());
        }

        if (rawValue instanceof String) {
            return (List<T>) io.smallrye.converters.Converters.newCollectionConverter(
                    (Converter<Object>) value -> ConvertedType.of(value).getAs(klass), ArrayList::new)
                    .convert((String) rawValue);
        }

        throw new IllegalStateException();
    }

    public <K, V> Map<K, V> getAsMap(Class<K> key, Class<V> value) {
        if (Map.class.isAssignableFrom(rawValue.getClass())) {
            return ((Set<Map.Entry>) ((Map) rawValue).entrySet())
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(ConvertedType.of(entry.getKey()).getAs(key),
                            ConvertedType.of(entry.getValue()).getAs(value)))
                    .collect(Collectors.toMap(entry -> (K) entry.getKey(),
                            entry -> (V) entry.getValue()));
        }

        throw new IllegalStateException();
    }

    public static ConvertedType of(final Object rawValue) {
        return new ConvertedType(rawValue, DEFAULT);
    }

    public static ConvertedType of(final Object rawValue, final Converters converters) {
        return new ConvertedType(rawValue, converters);
    }

    // This is so we have a single instance of Converters and we don't have to create a new one everytime we crete a ConvertedType.
    // We can extract this to a Factory and register all the additional converters.
    private static final Converters DEFAULT = new SmallRyeConvertersBuilder()
            // This does not sit here. Just for prototype.
            .withConverter(JsonObject.class, 100, new Converter<JsonObject>() {
                @Override
                public JsonObject convert(final String value) {
                    return Json.createReader(new StringReader(value)).readObject();
                }
            })
            .build();
}
