package io.smallrye.converters;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.Converter;

class CustomConverterTest {

    @Test
    void customInetAddressConverter() {
        SmallRyeConverters converters = buildConverters();
        InetAddress inetaddress = converters.getConverter(InetAddress.class).convert("10.0.0.1");
        assertNotNull(inetaddress);
        assertArrayEquals(new byte[] { 10, 0, 0, 1 }, inetaddress.getAddress());
    }

    @Test
    void characterConverter() {
        SmallRyeConverters converters = buildConverters();
        assertEquals('a', converters.convertValue("a", Character.class));
    }

    @Test
    void explicitConverter() {
        SmallRyeConverters converters = buildConverters();
        final Converter<Integer> customConverter = new Converter<Integer>() {
            public Integer convert(final String value) {
                return Integer.parseInt(value) * 2;
            }
        };

        assertEquals(1234, converters.convertValue("1234", Integer.class).intValue());
        assertEquals(2468, converters.convertValue("1234", customConverter).intValue());

        assertEquals(singletonList(1234), converters.convertValues("1234", Integer.class, ArrayList::new));
        assertEquals(singletonList(2468), converters.convertValues("1234", customConverter, ArrayList::new));

        assertThrows(NullPointerException.class, () -> converters.convertValue(null, Integer.class));
        // TODO - Should this also throw NPE? For collection it does, so it is inconsistent
        //assertThrows(NullPointerException.class, () -> converters.convertValue(null, customConverter));
        assertThrows(NullPointerException.class, () -> converters.convertValues(null, Integer.class, ArrayList::new));
        assertThrows(NullPointerException.class, () -> converters.convertValues(null, customConverter, ArrayList::new));
    }

    @Test
    void UUID() {
        String uuidStringTruth = "e4b3d0cf-55a2-4c01-a5d0-fe016fdc9195";
        String secondUuidStringTruth = "c2d88ee5-e981-4de2-ac54-8b887cc2acbc";
        UUID uuidUUIDTruth = UUID.fromString(uuidStringTruth);
        UUID secondUuidUUIDTruth = UUID.fromString(secondUuidStringTruth);
        SmallRyeConverters converters = buildConverters();

        assertEquals(uuidUUIDTruth, converters.convertValue(uuidStringTruth, UUID.class));

        assertThrows(NullPointerException.class, () -> converters.convertValue(null, UUID.class));
        // TODO - Check this one
        //assertNull(converters.convertValue(" ", UUID.class));

        assertEquals(uuidUUIDTruth, converters.convertValue(uuidStringTruth.toUpperCase(Locale.ROOT), UUID.class));

        ArrayList<UUID> values = converters.convertValues(uuidStringTruth + "," + secondUuidStringTruth, UUID.class,
                ArrayList::new);
        assertEquals(uuidUUIDTruth, values.get(0));
        assertEquals(secondUuidUUIDTruth, values.get(1));

        assertThrows(IllegalArgumentException.class, () -> converters.convertValue("invalid", UUID.class),
                "Malformed UUID should throw exception");
    }

    private static SmallRyeConverters buildConverters() {
        return new SmallRyeConvertersBuilder().build();
    }
}
