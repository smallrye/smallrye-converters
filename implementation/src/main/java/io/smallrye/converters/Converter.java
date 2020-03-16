package io.smallrye.converters;

import java.io.Serializable;

public interface Converter<T> extends Serializable {
    T convert(String value);
}
