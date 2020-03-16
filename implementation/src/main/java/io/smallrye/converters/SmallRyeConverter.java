package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmallRyeConverter {
    private static final SmallRyeConverter instance = new SmallRyeConverter();

    private final Map<Type, Converter<?>> converters;

    private SmallRyeConverter() {
        converters = new ConcurrentHashMap<>(Converters.ALL_CONVERTERS);
    }

    public static SmallRyeConverter getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> getConverter(Class<T> asType) {
        if (asType.isPrimitive()) {
            return (Converter<T>) getConverter(Converters.wrapPrimitiveType(asType));
        }
        if (asType.isArray()) {
            return Converters.newArrayConverter(getConverter(asType.getComponentType()), asType);
        }
        return (Converter<T>) converters.computeIfAbsent(asType, clazz -> {
            final Converter<?> conv = ImplicitConverters.getConverter((Class<?>) clazz);
            if (conv == null) {
                throw new IllegalArgumentException("No Converter registered for " + asType);
            }
            return conv;
        });
    }
}
