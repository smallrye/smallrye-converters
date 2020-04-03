package io.smallrye.converters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;

import io.smallrye.converters.api.Converter;
import io.smallrye.converters.api.InputConverter;
import io.smallrye.converters.api.OutputConverter;

public class SmallRyeConvertersBuilder {
    private final Map<Type, ConverterWithPriority> converters = new HashMap<>();

    private final Map<Type, InputConverter<?, ?>> inputConverters = new HashMap<>();
    private final Map<Type, OutputConverter<?, ?>> outputConverters = new HashMap<>();

    public SmallRyeConvertersBuilder() {
    }

    Map<Type, ConverterWithPriority> getConverters() {
        return converters;
    }

    Map<Type, InputConverter<?, ?>> getInputConverters() {
        return inputConverters;
    }

    Map<Type, OutputConverter<?, ?>> getOutputConverters() {
        return outputConverters;
    }

    public SmallRyeConverters build() {
        return new SmallRyeConverters(this);
    }

    public SmallRyeConvertersBuilder withConverters(Converter<?>[] converters) {
        for (Converter<?> converter : converters) {
            Type type = ConvertersUtils.getConverterType(converter.getClass());
            if (type == null) {
                throw ConverterMessages.msg.unableToAddConverter(converter);
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

    public <S, T> SmallRyeConvertersBuilder withInputConverter(
            Class<S> type,
            InputConverter<S, T> converter) {
        this.inputConverters.put(type, converter);
        return this;
    }

    public <S, T> SmallRyeConvertersBuilder withOutputConverter(
            Class<T> type,
            OutputConverter<S, T> converter) {
        this.outputConverters.put(type, converter);
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
