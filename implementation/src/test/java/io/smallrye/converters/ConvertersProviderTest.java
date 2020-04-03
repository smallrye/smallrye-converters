package io.smallrye.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.ConvertersProvider;

class ConvertersProviderTest {
    @Test
    void provider() {
        Assertions.assertNotNull(ConvertersProvider.getConverters());
    }
}
