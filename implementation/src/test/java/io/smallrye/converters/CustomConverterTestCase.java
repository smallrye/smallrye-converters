package io.smallrye.converters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;

import org.junit.Test;

public class CustomConverterTestCase {

    @Test
    public void testCustomInetAddressConverter() {
        SmallRyeConverters converters = buildConverters();
        InetAddress inetaddress = converters.getConverter(InetAddress.class).convert("10.0.0.1");
        assertNotNull(inetaddress);
        assertArrayEquals(new byte[] { 10, 0, 0, 1 }, inetaddress.getAddress());
    }

    @Test
    public void testCharacterConverter() {
        SmallRyeConverters converters = buildConverters();
        char c = converters.getConverter(Character.class).convert("a");
        assertEquals('a', c);
    }

    private static SmallRyeConverters buildConverters() {
        return new SmallRyeConvertersBuilder().build();
    }
}
