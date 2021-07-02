package io.smallrye.converters.api;

public final class ConvertersProvider {
    private ConvertersProvider() {
        throw new UnsupportedOperationException();
    }

    public static Converters getConverters() {
        return ConvertersProviderResolver.instance().getConverters();
    }

    public static Converters getConverters(final ClassLoader classLoader) {
        return ConvertersProviderResolver.instance().getConverters(classLoader);
    }
}
