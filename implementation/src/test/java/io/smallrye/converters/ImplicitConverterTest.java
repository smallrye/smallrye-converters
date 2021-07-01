package io.smallrye.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.Converter;

class ImplicitConverterTest {
    @Test
    public void implicitURLConverter() {
        final SmallRyeConverters converters = buildConverters();
        URL url = converters.getConverter(URL.class).convert("https://github.com/smallrye/smallrye-config/");
        assertNotNull(url);
        assertEquals("https", url.getProtocol());
        assertEquals("github.com", url.getHost());
        assertEquals("/smallrye/smallrye-config/", url.getPath());
    }

    @Test
    public void implicitLocalDateConverter() {
        SmallRyeConverters converters = buildConverters();
        LocalDate date = converters.getConverter(LocalDate.class).convert("2019-04-01");
        assertNotNull(date);
        assertEquals(2019, date.getYear());
        assertEquals(4, date.getMonthValue());
        assertEquals(1, date.getDayOfMonth());
    }

    @Test
    void serializationOfConstructorConverter() {
        Converter<File> converter = ImplicitConverters.getConverter(File.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(converter);
        } catch (IOException ex) {
            Assertions.fail("Constructor converter should be serializable, but could not serialize it: " + ex);
        }
        Object readObject = null;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
            readObject = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Assertions.fail(
                    "Constructor converter should be serializable, but could not deserialize a previously serialized instance: "
                            + ex);
        }
        assertEquals(converter.convert("/bad/path").getPath(),
                ((File) ((Converter) readObject).convert("/bad/path")).getPath(),
                "Converted values to have same file path");
    }

    private static SmallRyeConverters buildConverters() {
        return new SmallRyeConvertersBuilder().build();
    }
}
