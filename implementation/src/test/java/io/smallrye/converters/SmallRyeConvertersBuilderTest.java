package io.smallrye.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.annotation.Priority;

import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.Converter;

class SmallRyeConvertersBuilderTest {
    @Test
    public void withConverters() {
        final SmallRyeConverters converters = new SmallRyeConvertersBuilder()
                .withConverters(new Converter[] { new DummyConverter() }).build();

        final Converter<String> converter = converters.getConverter(String.class);
        assertNotNull(converter);
        assertEquals("dummy", converter.convert(""));
    }

    @Priority(1000)
    public static class DummyConverter implements Converter<String> {
        @Override
        public String convert(final String value) {
            return "dummy";
        }
    }
}
