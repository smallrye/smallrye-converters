package io.smallrye.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;

import org.junit.Test;

import io.smallrye.converters.api.Converter;

public class ImplicitConverterTestCase {

    @Test
    public void testImplicitURLConverter() {
        final SmallRyeConverters converters = buildConverters();
        URL url = converters.getConverter(URL.class).convert("https://github.com/smallrye/smallrye-config/");
        assertNotNull(url);
        assertEquals("https", url.getProtocol());
        assertEquals("github.com", url.getHost());
        assertEquals("/smallrye/smallrye-config/", url.getPath());
    }

    @Test
    public void testImplicitLocalDateConverter() {
        final SmallRyeConverters converters = buildConverters();
        LocalDate date = converters.getConverter(LocalDate.class).convert("2019-04-01");
        assertNotNull(date);
        assertEquals(2019, date.getYear());
        assertEquals(4, date.getMonthValue());
        assertEquals(1, date.getDayOfMonth());
    }

    private static SmallRyeConverters buildConverters() {
        return new SmallRyeConvertersBuilder().build();
    }

    @Test
    public void testSerializationOfConstructorConverter() {
        Converter<File> converter = ImplicitConverters.getConverter(File.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(converter);
        } catch (IOException ex) {
            fail("Constructor converter should be serializable, but could not serialize it: " + ex);
        }
        Object readObject = null;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
            readObject = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            fail("Constructor converter should be serializable, but could not deserialize a previously serialized instance: "
                    + ex);
        }
        assertEquals("Converted values to have same file path", converter.convert("/bad/path").getPath(),
                ((File) ((Converter) readObject).convert("/bad/path")).getPath());
    }
}
