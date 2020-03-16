package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;

public class SmallRyeConvertersBuilder {
    private Map<Type, ConverterWithPriority> converters = new HashMap<>();

    public SmallRyeConvertersBuilder() {
    }

    public Map<Type, ConverterWithPriority> getConverters() {
        return converters;
    }

    public SmallRyeConverters build() {
        return new SmallRyeConverters(this);
    }

    public SmallRyeConvertersBuilder withConverters(Converter<?>[] converters) {
        for (Converter<?> converter : converters) {
            Type type = Converters.getConverterType(converter.getClass());
            if (type == null) {
                throw new IllegalStateException(
                        "Can not add converter " + converter + " that is not parameterized with a type");
            }
            addConverter(type, getPriority(converter), converter, this.converters);
        }
        return this;
    }

    public <T> SmallRyeConvertersBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        addConverter(type, priority, converter, converters);
        return this;
    }

    public <T> SmallRyeConvertersBuilder withConverter(Type type, int priority, Converter<T> converter) {
        addConverter(type, priority, converter, converters);
        return this;
    }

    private static void addConverter(Type type, Converter converter, Map<Type, ConverterWithPriority> converters) {
        addConverter(type, getPriority(converter), converter, converters);
    }

    private static void addConverter(Type type, int priority, Converter converter,
            Map<Type, ConverterWithPriority> converters) {
        // add the converter only if it has a higher priority than another converter for the same type
        ConverterWithPriority oldConverter = converters.get(type);
        int newPriority = getPriority(converter);
        if (oldConverter == null || priority > oldConverter.priority) {
            converters.put(type, new ConverterWithPriority(converter, newPriority));
        }
    }

    private static int getPriority(Converter<?> converter) {
        int priority = 100;
        Priority priorityAnnotation = converter.getClass().getAnnotation(Priority.class);
        if (priorityAnnotation != null) {
            priority = priorityAnnotation.value();
        }
        return priority;
    }

    static class ConverterWithPriority {
        private final Converter converter;
        private final int priority;

        private ConverterWithPriority(Converter converter, int priority) {
            this.converter = converter;
            this.priority = priority;
        }

        Converter getConverter() {
            return converter;
        }

        int getPriority() {
            return priority;
        }
    }
}
