package io.smallrye.converters.type;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;

import org.eclipse.yasson.YassonJsonb;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class InputOutputConverters {
    @Test
    public void integer() {
        final ConvertedType value = new ConvertedType("1");
        assertEquals(1, new IntegerOutputConverter().convert(value).intValue());
    }

    @Test
    public void jsonObject() {
        final JsonObject jsonObject = Json.createObjectBuilder().add("street", "abc def").add("code", 1234)
                .add("country", "ABC").build();

        final ConvertedType value = new ConvertedType(jsonObject);
        final Address address = new AddressOutputConverter().convert(value);
        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());
    }

    @Test
    public void jsonAsString() {
        final String json = Json.createObjectBuilder()
                .add("street", "abc def")
                .add("code", 1234)
                .add("country", "ABC")
                .build()
                .toString();

        final ConvertedType value = new ConvertedType(json);
        final Address address = new AddressOutputConverter().convert(value);
        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());
    }

    @Test
    public void yaml() {
        final String yml = "street: abc def\n" + "code: 1234\n" + "country: ABC\n";

        final ConvertedType value = new ConvertedType(yml);
        final OutputConverter<Address> outputConverter = new OutputConverter<Address>() {
            @Override
            public Address convert(final ConvertedType value) {
                return (Address) new Yaml(new Constructor(Address.class)).load(value.getAsString());
            }
        };
        final Address address = outputConverter.convert(value);

        assertEquals("abc def", address.getStreet());
        assertEquals(1234, address.getCode().intValue());
        assertEquals("ABC", address.getCountry());
    }

    public static class IntegerOutputConverter implements OutputConverter<Integer> {
        @Override
        public Integer convert(final ConvertedType value) {
            return value.getAsNumber().intValue();
        }
    }

    public static class AddressOutputConverter implements OutputConverter<Address> {
        @Override
        public Address convert(final ConvertedType value) {
            final YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create();
            return jsonb.fromJsonStructure(value.getAs(JsonObject.class), Address.class);
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
