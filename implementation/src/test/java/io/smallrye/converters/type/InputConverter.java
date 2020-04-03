package io.smallrye.converters.type;

import java.io.Serializable;

public interface InputConverter<T> extends Serializable {
    ConvertedType convert(T value);
}
