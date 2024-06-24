package io.smallrye.converters;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.Converters;
import io.smallrye.converters.api.ConvertersProvider;

@Disabled
class ConvertedTypeTest {
    @Test
    void stringToNumbers() {
        Converters converters = ConvertersProvider.getConverters();

        assertEquals(1, converters.from("1").getAs(Byte.class).byteValue());
        assertEquals(1, ConvertedTypeImpl.of("1").getAs(Short.class).shortValue());
        assertEquals(1, ConvertedTypeImpl.of("1").getAs(Integer.class).intValue());
        assertEquals(9999999999L, ConvertedTypeImpl.of("9999999999").getAs(Long.class).longValue());
        assertEquals(10.1f, ConvertedTypeImpl.of("10.10").getAs(Float.class), 0);
        assertEquals(10.1d, ConvertedTypeImpl.of("10.10").getAs(Double.class), 0);

        assertEquals(1, ConvertedTypeImpl.of("1").getAsNumber().byteValue());
        assertEquals(1, ConvertedTypeImpl.of("1").getAsNumber().shortValue());
        assertEquals(1, ConvertedTypeImpl.of("1").getAsNumber().intValue());
        assertEquals(9999999999L, ConvertedTypeImpl.of("9999999999").getAsNumber().longValue());
        assertEquals(10.1f, ConvertedTypeImpl.of("10.10").getAsNumber().floatValue(), 0);
        assertEquals(10.1d, ConvertedTypeImpl.of("10.10").getAsNumber().doubleValue(), 0);
    }

    @Test
    void numbersToString() {
        assertEquals("1", ConvertedTypeImpl.of((byte) 1).getAs(String.class));
        assertEquals("1", ConvertedTypeImpl.of((short) 1).getAs(String.class));
        assertEquals("1", ConvertedTypeImpl.of(1).getAs(String.class));
        assertEquals("9999999999", ConvertedTypeImpl.of(9999999999L).getAs(String.class));
        assertEquals("10.1", ConvertedTypeImpl.of(10.10f).getAs(String.class));
        assertEquals("10.1", ConvertedTypeImpl.of(10.10d).getAs(String.class));
    }

    @Test
    void booleans() {
        assertTrue(ConvertedTypeImpl.of("true").getAs(Boolean.class));
        assertTrue(ConvertedTypeImpl.of("true").getAsBoolean());
        assertEquals("true", ConvertedTypeImpl.of(Boolean.TRUE).getAsString());
    }

    @Test
    void arrays() {
        String[] strings = ConvertedTypeImpl.of(new String[] { "1", "2", "3" }).getAs(String[].class);
        assertTrue(Arrays.asList(strings).contains("1"));
        assertTrue(Arrays.asList(strings).contains("2"));
        assertTrue(Arrays.asList(strings).contains("3"));

        /*
         * Integer[] integers = ConvertedType.of(new String[]{"1", "2", "3"}).getAs(Integer[].class);
         * assertTrue(Arrays.asList(integers).contains(1));
         * assertTrue(Arrays.asList(integers).contains(2));
         * assertTrue(Arrays.asList(integers).contains(3));
         */
    }

    @Test
    void lists() {
        List<String> strings = ConvertedTypeImpl.of(Stream.of("1", "2", "3").collect(toList())).getAsList(String.class);
        assertTrue(strings.contains("1"));
        assertTrue(strings.contains("2"));
        assertTrue(strings.contains("3"));

        List<Integer> integers = ConvertedTypeImpl.of(Stream.of(1, 2, 3).collect(toList())).getAsList(Integer.class);
        assertTrue(integers.contains(1));
        assertTrue(integers.contains(2));
        assertTrue(integers.contains(3));

        List<String> intsAsStrings = ConvertedTypeImpl.of(Stream.of(1, 2, 3).collect(toList())).getAsList(String.class);
        assertTrue(intsAsStrings.contains("1"));
        assertTrue(intsAsStrings.contains("2"));
        assertTrue(intsAsStrings.contains("3"));

        List<Integer> stringsAsInts = ConvertedTypeImpl.of(Stream.of("1", "2", "3").collect(toList()))
                .getAsList(Integer.class);
        assertTrue(stringsAsInts.contains(1));
        assertTrue(stringsAsInts.contains(2));
        assertTrue(stringsAsInts.contains(3));
    }

    @Test
    void stringAsList() {
        List<String> strings = ConvertedTypeImpl.of("1,2,3").getAsList(String.class);
        assertTrue(strings.contains("1"));
        assertTrue(strings.contains("2"));
        assertTrue(strings.contains("3"));

        List<Integer> integers = ConvertedTypeImpl.of("1,2,3").getAsList(Integer.class);
        assertTrue(integers.contains(1));
        assertTrue(integers.contains(2));
        assertTrue(integers.contains(3));
    }

    @Test
    void maps() {
        Map<String, String> strings = ConvertedTypeImpl.of(new HashMap<String, String>() {
            {
                put("1", "a");
                put("2", "b");
            }
        }).getAsMap(String.class, String.class);
        assertTrue(strings.containsKey("1"));
        assertTrue(strings.containsKey("2"));
        assertEquals("a", strings.get("1"));
        assertEquals("b", strings.get("2"));

        Map<Integer, String> stringInts = ConvertedTypeImpl.of(new HashMap<String, String>() {
            {
                put("1", "a");
                put("2", "b");
            }
        }).getAsMap(Integer.class, String.class);
        assertTrue(stringInts.containsKey(1));
        assertTrue(stringInts.containsKey(2));
        assertEquals("a", stringInts.get(1));
        assertEquals("b", stringInts.get(2));

        Map<String, String> intsAsStrings = ConvertedTypeImpl.of(new HashMap<Integer, Integer>() {
            {
                put(1, 1);
                put(2, 2);
            }
        }).getAsMap(String.class, String.class);
        assertTrue(intsAsStrings.containsKey("1"));
        assertTrue(intsAsStrings.containsKey("2"));
        assertEquals("1", intsAsStrings.get("1"));
        assertEquals("2", intsAsStrings.get("2"));
    }
}
