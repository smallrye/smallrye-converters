package io.smallrye.converters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;

import org.junit.Test;

public class CustomConverterTestCase {

    @Test
    public void testCustomInetAddressConverter() {
        InetAddress inetaddress = SmallRyeConverter.getInstance().getConverter(InetAddress.class).convert("10.0.0.1");
        assertNotNull(inetaddress);
        assertArrayEquals(new byte[] { 10, 0, 0, 1 }, inetaddress.getAddress());
    }

    @Test
    public void testCharacterConverter() {
        char c = SmallRyeConverter.getInstance().getConverter(Character.class).convert("a");
        assertEquals('a', c);
    }
}
