package io.smallrye.converters;

import java.io.Serializable;

import io.smallrye.converters.api.Converter;

/**
 * A basic converter.
 */
public abstract class AbstractConverter<T> implements Converter<T>, Serializable {
    private static final long serialVersionUID = 6881031847338407885L;
}
