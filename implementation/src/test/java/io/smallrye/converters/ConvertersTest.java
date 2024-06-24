/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.converters;

import static io.smallrye.converters.ConvertersUtils.getImplicitConverter;
import static io.smallrye.converters.ConvertersUtils.maximumValueConverter;
import static io.smallrye.converters.ConvertersUtils.minimumValueConverter;
import static io.smallrye.converters.ConvertersUtils.newArrayConverter;
import static io.smallrye.converters.ConvertersUtils.newCollectionConverter;
import static io.smallrye.converters.ConvertersUtils.newEmptyValueConverter;
import static io.smallrye.converters.ConvertersUtils.newOptionalConverter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.Converter;

class ConvertersTest {
    @Test
    void collectionConverters() {
        Converter<Collection<String>> conv = newCollectionConverter(getImplicitConverter(String.class), ArrayList::new);
        assertNull(conv.convert(""));
        assertEquals(Collections.singletonList("foo"), conv.convert("foo"));
        assertEquals(Arrays.asList("foo", "bar"), conv.convert("foo,,bar,,"));

        Converter<Collection<String>> conv2 = newEmptyValueConverter(conv, Collections.emptyList());
        assertEquals(new ArrayList<>(), conv2.convert(""));
        assertEquals(Collections.singletonList("foo"), conv2.convert("foo"));
        assertEquals(Arrays.asList("foo", "bar"), conv2.convert("foo,,bar,,"));

        Converter<Optional<Collection<String>>> conv3 = newOptionalConverter(conv);
        assertFalse(conv3.convert("").isPresent());
        assertEquals(Collections.singletonList("foo"), conv3.convert("foo").orElse(null));
        assertEquals(Arrays.asList("foo", "bar"), conv3.convert("foo,,bar,,").orElse(null));
    }

    @Test
    void arrayConverters() {
        Converter<String[]> conv = newArrayConverter(getImplicitConverter(String.class), String[].class);
        assertNull(conv.convert(""));
        assertArrayEquals(array("foo"), conv.convert("foo"));
        assertArrayEquals(array("foo", "bar"), conv.convert("foo,,bar,,"));

        Converter<String[]> conv2 = newEmptyValueConverter(conv, new String[0]);
        assertArrayEquals(array(), conv2.convert(""));
        assertArrayEquals(array("foo"), conv2.convert("foo"));
        assertArrayEquals(array("foo", "bar"), conv2.convert("foo,,bar,,"));

        Converter<Optional<String[]>> conv3 = newOptionalConverter(conv);
        assertFalse(conv3.convert("").isPresent());
        assertArrayEquals(array("foo"), conv3.convert("foo").orElse(null));
        assertArrayEquals(array("foo", "bar"), conv3.convert("foo,,bar,,").orElse(null));
    }

    @Test
    void minimumValue() {
        Converter<Integer> intConv = getImplicitConverter(Integer.class);
        Converter<Integer> intMin2Conv = minimumValueConverter(intConv, 2, true);
        Converter<Integer> intMin2ExConv = minimumValueConverter(intConv, 2, false);
        Converter<Integer> intMin3Conv = minimumValueConverter(intConv, 3, true);
        try {
            intMin3Conv.convert("2");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(2, intMin2Conv.convert("2").intValue());
        try {
            intMin2ExConv.convert("2");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        Converter<String> strConv = getImplicitConverter(String.class);
        Converter<String> strConv1 = minimumValueConverter(strConv, "aardvark", true);
        Converter<String> strConv2 = minimumValueConverter(strConv, "anteater", true);
        Converter<String> strConv3 = minimumValueConverter(strConv, "anteater", false);
        Converter<String> strConv4 = minimumValueConverter(strConv, "antelope", true);
        assertEquals("anteater", strConv1.convert("anteater"));
        assertEquals("anteater", strConv2.convert("anteater"));
        try {
            strConv3.convert("anteater");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        try {
            strConv4.convert("anteater");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        Converter<LocalDate> dateConv = getImplicitConverter(LocalDate.class);
        Converter<ChronoLocalDate> dateConv1 = minimumValueConverter(dateConv, LocalDate.of(1950, 1, 1), true);
        Converter<ChronoLocalDate> dateConv2 = minimumValueConverter(dateConv, LocalDate.of(1950, 1, 1),
                false);
        Converter<ChronoLocalDate> dateConv3 = minimumValueConverter(dateConv, LocalDate.of(1949, 12, 31),
                true);
        Converter<ChronoLocalDate> dateConv4 = minimumValueConverter(dateConv, LocalDate.of(1950, 1, 2), true);
        assertEquals(LocalDate.of(1950, 1, 1), dateConv1.convert("1950-01-01"));
        try {
            dateConv2.convert("1950-01-01");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(LocalDate.of(1950, 1, 1), dateConv3.convert("1950-01-01"));
        try {
            dateConv4.convert("1950-01-01");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void maximumValue() {
        Converter<Integer> intConv = getImplicitConverter(Integer.class);
        Converter<Integer> intMax2Conv = maximumValueConverter(intConv, 2, true);
        Converter<Integer> intMax2ExConv = maximumValueConverter(intConv, 2, false);
        Converter<Integer> intMax3Conv = maximumValueConverter(intConv, 3, true);
        assertEquals(2, intMax3Conv.convert("2").intValue());
        assertEquals(2, intMax2Conv.convert("2").intValue());
        try {
            intMax2ExConv.convert("2");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        Converter<String> strConv = getImplicitConverter(String.class);
        Converter<String> strConv1 = maximumValueConverter(strConv, "aardvark", true);
        Converter<String> strConv2 = maximumValueConverter(strConv, "anteater", true);
        Converter<String> strConv3 = maximumValueConverter(strConv, "anteater", false);
        Converter<String> strConv4 = maximumValueConverter(strConv, "antelope", true);
        try {
            strConv1.convert("anteater");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        assertEquals("anteater", strConv2.convert("anteater"));
        try {
            strConv3.convert("anteater");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        assertEquals("anteater", strConv4.convert("anteater"));
        Converter<LocalDate> dateConv = getImplicitConverter(LocalDate.class);
        Converter<ChronoLocalDate> dateConv1 = maximumValueConverter(dateConv, LocalDate.of(1950, 1, 1), true);
        Converter<ChronoLocalDate> dateConv2 = maximumValueConverter(dateConv, LocalDate.of(1950, 1, 1),
                false);
        Converter<ChronoLocalDate> dateConv3 = maximumValueConverter(dateConv, LocalDate.of(1949, 12, 31),
                true);
        Converter<ChronoLocalDate> dateConv4 = maximumValueConverter(dateConv, LocalDate.of(1950, 1, 2), true);
        assertEquals(LocalDate.of(1950, 1, 1), dateConv1.convert("1950-01-01"));
        try {
            dateConv2.convert("1950-01-01");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        try {
            dateConv3.convert("1950-01-01");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(LocalDate.of(1950, 1, 1), dateConv4.convert("1950-01-01"));
    }

    @Test
    void empty() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        assertTrue(converters.convert("1234", OptionalInt.class).isPresent());
        assertFalse(converters.convert("", OptionalInt.class).isPresent());
        assertThrows(NullPointerException.class, () -> converters.convert(null, OptionalInt.class));
    }

    @Test
    void shortValue() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();
        short expected = 2;
        assertEquals(expected, (short) converters.convert("2", Short.class), "Unexpected value for short config");
        assertEquals(expected, (short) converters.convert("2", Short.TYPE), "Unexpected value for short config");
    }

    @Test
    void byteValue() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();
        byte expected = 2;
        assertEquals(expected, (byte) converters.convert("2", Byte.class), "Unexpected value for byte config");
        assertEquals(expected, (byte) converters.convert("2", Byte.TYPE), "Unexpected value for byte config");
    }

    @Test
    void byteArray() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder()
                .withConverter(byte[].class, 1000, (Converter<byte[]>) value -> Base64.getDecoder().decode(value.getBytes()))
                .build();

        String value = Base64.getEncoder().encodeToString("bytes".getBytes());

        assertEquals("Ynl0ZXM=", converters.convert(value, v -> v));
        assertEquals("bytes", new String(converters.convert(value, byte[].class)));
    }

    @Test
    void currency() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        Currency expected = Currency.getInstance("GBP");
        assertEquals(expected.getCurrencyCode(),
                converters.convert("GBP", Currency.class).getCurrencyCode(),
                "Unexpected value for byte config");
    }

    @Test
    void bitSet() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        BitSet expected = new BitSet(8);
        expected.set(1);
        expected.set(3);
        expected.set(5);
        expected.set(7);
        assertEquals(expected.toString(), converters.convert("AA", BitSet.class).toString(),
                "Unexpected value for byte config");
    }

    @Test
    void pattern() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        Pattern expected = Pattern.compile("[0-9]");
        assertEquals(expected.pattern(), converters.convert("[0-9]", Pattern.class).pattern(),
                "Unexpected value for pattern");
    }

    @Test
    void nulls() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        assertThrows(NullPointerException.class, () -> converters.getConverter(Boolean.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Byte.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Short.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Integer.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Long.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Float.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Double.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(Character.class).convert(null));

        assertThrows(NullPointerException.class, () -> converters.getConverter(OptionalInt.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(OptionalLong.class).convert(null));
        assertThrows(NullPointerException.class, () -> converters.getConverter(OptionalDouble.class).convert(null));
    }

    @SafeVarargs
    private static <T> T[] array(T... items) {
        return items;
    }
}
