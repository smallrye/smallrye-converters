package io.smallrye.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;

import org.eclipse.yasson.YassonJsonb;
import org.junit.jupiter.api.Test;

import io.smallrye.converters.api.ConvertedType;
import io.smallrye.converters.api.InputConverter;
import io.smallrye.converters.api.OutputConverter;

class SmallRyeConvertersTest {
    @Test
    void api() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder().build();

        assertNotNull(converters.getConverter(Integer.class));
        assertNotNull(converters.getOptionalConverter(Integer.class));
        assertEquals(1, converters.convert("1", Integer.class).intValue());
        assertEquals("dummy", converters.convert("you", value -> "dummy"));
    }

    @Test
    void convertedType() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder()
                .withInputConverter(String.class, new JsonObjectInputConverter())
                .withOutputConverter(Address.class, new AddressOutputConverter())
                .build();
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("street", "abc def")
                .add("code", 1234)
                .add("country", "ABC")
                .build();

        ConvertedType<JsonObject> fromString = converters.from("jsonObject");
        Address address = fromString.getAs(Address.class);
        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());

        ConvertedType<JsonObject> fromJsonObject = converters.from(jsonObject);
        address = fromJsonObject.getAs(Address.class);
        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());
    }

    @Test
    void inputPassthrough() {
        SmallRyeConverters converters = new SmallRyeConvertersBuilder()
                .withOutputConverter(Address.class, new AddressOutputConverter())
                .build();
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("street", "abc def")
                .add("code", 1234)
                .add("country", "ABC")
                .build();

        ConvertedType<JsonObject> fromJsonObject = converters.from(jsonObject);
        Address address = fromJsonObject.getAs(Address.class);
        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());
    }

    static class JsonObjectInputConverter implements InputConverter<String, JsonObject> {
        @Override
        public JsonObject convert(final String value) {
            return Json.createObjectBuilder()
                    .add("street", "abc def")
                    .add("code", 1234)
                    .add("country", "ABC")
                    .build();
        }
    }

    static class AddressOutputConverter implements OutputConverter<JsonObject, Address> {
        @Override
        public Address convert(final ConvertedType<JsonObject> value) {
            YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create();
            return jsonb.fromJsonStructure(value.getRaw(), Address.class);
        }
    }

    public static class Address {
        private String street;
        private Integer code;
        private String country;

        public Address() {
        }

        public Address(final String street, final Integer code, final String country) {
            this.street = street;
            this.code = code;
            this.country = country;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(final String street) {
            this.street = street;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(final Integer code) {
            this.code = code;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(final String country) {
            this.country = country;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", code=" + code +
                    ", country='" + country + '\'' +
                    '}';
        }
    }
}
