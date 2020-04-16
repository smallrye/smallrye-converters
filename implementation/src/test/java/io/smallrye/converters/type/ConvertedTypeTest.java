package io.smallrye.converters.type;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    @Test
    public void arrays() {
        final String[] strings = ConvertedType.of(new String[] { "1", "2", "3" }).getAs(String[].class);
        assertTrue(Arrays.asList(strings).contains("1"));
        assertTrue(Arrays.asList(strings).contains("2"));
        assertTrue(Arrays.asList(strings).contains("3"));

        /*
         * final Integer[] integers = ConvertedType.of(new String[]{"1", "2", "3"}).getAs(Integer[].class);
         * assertTrue(Arrays.asList(integers).contains(1));
         * assertTrue(Arrays.asList(integers).contains(2));
         * assertTrue(Arrays.asList(integers).contains(3));
         */
    }

    @Test
    public void lists() {
        final List<String> strings = ConvertedType.of(Stream.of("1", "2", "3").collect(toList())).getAsList(String.class);
        assertTrue(strings.contains("1"));
        assertTrue(strings.contains("2"));
        assertTrue(strings.contains("3"));

        final List<Integer> integers = ConvertedType.of(Stream.of(1, 2, 3).collect(toList())).getAsList(Integer.class);
        assertTrue(integers.contains(1));
        assertTrue(integers.contains(2));
        assertTrue(integers.contains(3));

        final List<String> intsAsStrings = ConvertedType.of(Stream.of(1, 2, 3).collect(toList())).getAsList(String.class);
        assertTrue(intsAsStrings.contains("1"));
        assertTrue(intsAsStrings.contains("2"));
        assertTrue(intsAsStrings.contains("3"));

        final List<Integer> stringsAsInts = ConvertedType.of(Stream.of("1", "2", "3").collect(toList()))
                .getAsList(Integer.class);
        assertTrue(stringsAsInts.contains(1));
        assertTrue(stringsAsInts.contains(2));
        assertTrue(stringsAsInts.contains(3));
    }

    @Test
    public void stringAsList() {
        final List<String> strings = ConvertedType.of("1,2,3").getAsList(String.class);
        assertTrue(strings.contains("1"));
        assertTrue(strings.contains("2"));
        assertTrue(strings.contains("3"));

        final List<Integer> integers = ConvertedType.of("1,2,3").getAsList(Integer.class);
        assertTrue(integers.contains(1));
        assertTrue(integers.contains(2));
        assertTrue(integers.contains(3));
    }

    @Test
    public void maps() {
        final Map<String, String> strings = ConvertedType.of(new HashMap<String, String>() {
            {
                put("1", "a");
                put("2", "b");
            }
        }).getAsMap(String.class, String.class);
        assertTrue(strings.containsKey("1"));
        assertTrue(strings.containsKey("2"));
        assertEquals("a", strings.get("1"));
        assertEquals("b", strings.get("2"));

        final Map<Integer, String> stringInts = ConvertedType.of(new HashMap<String, String>() {
            {
                put("1", "a");
                put("2", "b");
            }
        }).getAsMap(Integer.class, String.class);
        assertTrue(stringInts.containsKey(1));
        assertTrue(stringInts.containsKey(2));
        assertEquals("a", stringInts.get(1));
        assertEquals("b", stringInts.get(2));

        final Map<String, String> intsAsStrings = ConvertedType.of(new HashMap<Integer, Integer>() {
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
