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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.eclipse.microprofile.config.spi.Converter;
import org.junit.Test;

public class ConvertersTestCase {

    @Test
    public void testCollection() {
        final Converter<Collection<String>> conv = Converters
                .newCollectionConverter(Converters.getImplicitConverter(String.class), ArrayList::new);
        assertNull(conv.convert(""));
        assertEquals(Collections.singletonList("foo"), conv.convert("foo"));
        assertEquals(Arrays.asList("foo", "bar"), conv.convert("foo,,bar,,"));

        final Converter<Collection<String>> conv2 = Converters.newEmptyValueConverter(conv, Collections.emptyList());
        assertEquals(new ArrayList<>(), conv2.convert(""));
        assertEquals(Collections.singletonList("foo"), conv2.convert("foo"));
        assertEquals(Arrays.asList("foo", "bar"), conv2.convert("foo,,bar,,"));

        final Converter<Optional<Collection<String>>> conv3 = Converters.newOptionalConverter(conv);
        assertFalse(conv3.convert("").isPresent());
        assertEquals(Collections.singletonList("foo"), conv3.convert("foo").orElse(null));
        assertEquals(Arrays.asList("foo", "bar"), conv3.convert("foo,,bar,,").orElse(null));
    }

    @Test
    public void testArray() {
        final Converter<String[]> conv = Converters
                .newArrayConverter(Converters.getImplicitConverter(String.class), String[].class);
        assertNull(conv.convert(""));
        assertArrayEquals(array("foo"), conv.convert("foo"));
        assertArrayEquals(array("foo", "bar"), conv.convert("foo,,bar,,"));

        final Converter<String[]> conv2 = Converters.newEmptyValueConverter(conv, new String[0]);
        assertArrayEquals(array(), conv2.convert(""));
        assertArrayEquals(array("foo"), conv2.convert("foo"));
        assertArrayEquals(array("foo", "bar"), conv2.convert("foo,,bar,,"));

        final Converter<Optional<String[]>> conv3 = Converters.newOptionalConverter(conv);
        assertFalse(conv3.convert("").isPresent());
        assertArrayEquals(array("foo"), conv3.convert("foo").orElse(null));
        assertArrayEquals(array("foo", "bar"), conv3.convert("foo,,bar,,").orElse(null));
    }

    @Test
    public void testMinimumValue() {
        final Converter<Integer> intConv = Converters.getImplicitConverter(Integer.class);
        final Converter<Integer> intMin2Conv = Converters.minimumValueConverter(intConv, Integer.valueOf(2), true);
        final Converter<Integer> intMin2ExConv = Converters.minimumValueConverter(intConv, Integer.valueOf(2), false);
        final Converter<Integer> intMin3Conv = Converters.minimumValueConverter(intConv, Integer.valueOf(3), true);
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
        final Converter<String> strConv = Converters.getImplicitConverter(String.class);
        final Converter<String> strConv1 = Converters.minimumValueConverter(strConv, "aardvark", true);
        final Converter<String> strConv2 = Converters.minimumValueConverter(strConv, "anteater", true);
        final Converter<String> strConv3 = Converters.minimumValueConverter(strConv, "anteater", false);
        final Converter<String> strConv4 = Converters.minimumValueConverter(strConv, "antelope", true);
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
        final Converter<LocalDate> dateConv = Converters.getImplicitConverter(LocalDate.class);
        final Converter<ChronoLocalDate> dateConv1 = Converters.minimumValueConverter(dateConv, LocalDate.of(1950, 1, 1), true);
        final Converter<ChronoLocalDate> dateConv2 = Converters.minimumValueConverter(dateConv, LocalDate.of(1950, 1, 1),
                false);
        final Converter<ChronoLocalDate> dateConv3 = Converters.minimumValueConverter(dateConv, LocalDate.of(1949, 12, 31),
                true);
        final Converter<ChronoLocalDate> dateConv4 = Converters.minimumValueConverter(dateConv, LocalDate.of(1950, 1, 2), true);
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
    public void testMaximumValue() {
        final Converter<Integer> intConv = Converters.getImplicitConverter(Integer.class);
        final Converter<Integer> intMax2Conv = Converters.maximumValueConverter(intConv, Integer.valueOf(2), true);
        final Converter<Integer> intMax2ExConv = Converters.maximumValueConverter(intConv, Integer.valueOf(2), false);
        final Converter<Integer> intMax3Conv = Converters.maximumValueConverter(intConv, Integer.valueOf(3), true);
        assertEquals(2, intMax3Conv.convert("2").intValue());
        assertEquals(2, intMax2Conv.convert("2").intValue());
        try {
            intMax2ExConv.convert("2");
            fail("Expected exception");
        } catch (IllegalArgumentException expected) {
        }
        final Converter<String> strConv = Converters.getImplicitConverter(String.class);
        final Converter<String> strConv1 = Converters.maximumValueConverter(strConv, "aardvark", true);
        final Converter<String> strConv2 = Converters.maximumValueConverter(strConv, "anteater", true);
        final Converter<String> strConv3 = Converters.maximumValueConverter(strConv, "anteater", false);
        final Converter<String> strConv4 = Converters.maximumValueConverter(strConv, "antelope", true);
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
        final Converter<LocalDate> dateConv = Converters.getImplicitConverter(LocalDate.class);
        final Converter<ChronoLocalDate> dateConv1 = Converters.maximumValueConverter(dateConv, LocalDate.of(1950, 1, 1), true);
        final Converter<ChronoLocalDate> dateConv2 = Converters.maximumValueConverter(dateConv, LocalDate.of(1950, 1, 1),
                false);
        final Converter<ChronoLocalDate> dateConv3 = Converters.maximumValueConverter(dateConv, LocalDate.of(1949, 12, 31),
                true);
        final Converter<ChronoLocalDate> dateConv4 = Converters.maximumValueConverter(dateConv, LocalDate.of(1950, 1, 2), true);
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

    @SafeVarargs
    private static <T> T[] array(T... items) {
        return items;
    }
}
