package io.smallrye.converters.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConvertedTypeTest {
    @Test
    public void stringToNumbers() {
        assertEquals(1, ConvertedType.of("1").getAs(Byte.class).byteValue());
        assertEquals(1, ConvertedType.of("1").getAs(Short.class).shortValue());
        assertEquals(1, ConvertedType.of("1").getAs(Integer.class).intValue());
        assertEquals(9999999999L, ConvertedType.of("9999999999").getAs(Long.class).longValue());
        assertEquals(10.1f, ConvertedType.of("10.10").getAs(Float.class), 0);
        assertEquals(10.1d, ConvertedType.of("10.10").getAs(Double.class), 0);

        assertEquals(1, ConvertedType.of("1").getAsNumber().byteValue());
        assertEquals(1, ConvertedType.of("1").getAsNumber().shortValue());
        assertEquals(1, ConvertedType.of("1").getAsNumber().intValue());
        assertEquals(9999999999L, ConvertedType.of("9999999999").getAsNumber().longValue());
        assertEquals(10.1f, ConvertedType.of("10.10").getAsNumber().floatValue(), 0);
        assertEquals(10.1d, ConvertedType.of("10.10").getAsNumber().doubleValue(), 0);
    }

    @Test
    public void numbersToString() {
        assertEquals("1", ConvertedType.of((byte) 1).getAs(String.class));
        assertEquals("1", ConvertedType.of((short) 1).getAs(String.class));
        assertEquals("1", ConvertedType.of(1).getAs(String.class));
        assertEquals("9999999999", ConvertedType.of(9999999999L).getAs(String.class));
        assertEquals("10.1", ConvertedType.of(10.10f).getAs(String.class));
        assertEquals("10.1", ConvertedType.of(10.10d).getAs(String.class));
    }

    @Test
    public void booleans() {
        assertTrue(ConvertedType.of("true").getAs(Boolean.class));
        assertTrue(ConvertedType.of("true").getAsBoolean());
        assertEquals("true", ConvertedType.of(Boolean.TRUE).getAsString());
    }
}
