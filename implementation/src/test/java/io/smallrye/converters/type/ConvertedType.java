package io.smallrye.converters.type;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import io.smallrye.converters.SmallRyeConverters;
import io.smallrye.converters.SmallRyeConvertersBuilder;
import io.smallrye.converters.api.Converter;

public class ConvertedType {
    private final Object rawValue;

    private final SmallRyeConverters converters = new SmallRyeConvertersBuilder()
            // This does not sit here. Just for prototype.
            .withConverter(JsonObject.class, 100, new Converter<JsonObject>() {
                @Override
                public JsonObject convert(final String value) {
                    return Json.createReader(new StringReader(value)).readObject();
                }
            })
            .build();

    public ConvertedType(final Object value) {
        this.rawValue = value;
    }

    public Object getRawValue() {
        return rawValue;
    }

    public String getAsString() {
        return rawValue.toString();
    }

    public Number getAsNumber() {
        if (rawValue instanceof Number) {
            return (Number) rawValue;
        }

        final Converter<Double> converter = converters.getConverter(Double.class);
        return converter != null ? converter.convert(rawValue.toString()) : null;
    }

    public Boolean getAsBoolean() {
        return null;
    }

    // TODO - add more suppoted types

    public <T> T getAs(Class<T> klass) {
        if (klass.isInstance(rawValue)) {
            return (T) rawValue;
        }

        final Converter<T> converter = converters.getConverter(klass);
        return converter != null ? converter.convert(rawValue.toString()) : null;
    }
}
