package io.smallrye.converters.type;

import java.io.Serializable;

public interface OutputConverter<T> extends Serializable {
    T convert(ConvertedType value);
}
