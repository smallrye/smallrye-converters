package io.smallrye.converters;

import static org.junit.Assert.*;

import org.junit.Test;

import io.smallrye.converters.api.Converter;

public class SmallRyeConvertersTest {
    @Test
    public void api() {
        final SmallRyeConverters converters = new SmallRyeConverters();

        assertNotNull(converters.getConverter(Integer.class));
        assertNotNull(converters.getOptionalConverter(Integer.class));
        assertEquals(1, converters.convertValue("1", Integer.class).intValue());
        assertEquals("dummy", converters.convertValue("you", (Converter<String>) value -> "dummy"));
    }
}
