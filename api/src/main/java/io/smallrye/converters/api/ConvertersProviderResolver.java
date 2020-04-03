package io.smallrye.converters.api;

import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class ConvertersProviderResolver {
    private static volatile ConvertersProviderResolver instance = null;

    public abstract Converters getConverters();

    public abstract Converters getConverters(ClassLoader classLoader);

    protected ConvertersProviderResolver() {

    }

    public static ConvertersProviderResolver instance() {
        if (instance == null) {
            synchronized (ConvertersProviderResolver.class) {
                if (instance != null) {
                    return instance;
                }
                instance = loadSpi(ConvertersProviderResolver.class.getClassLoader());
            }
        }

        return instance;
    }

    private static ConvertersProviderResolver loadSpi(ClassLoader cl) {
        ServiceLoader<ConvertersProviderResolver> sl = ServiceLoader.load(
                ConvertersProviderResolver.class, cl);
        final Iterator<ConvertersProviderResolver> iterator = sl.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new IllegalStateException(
                "No ConvertersProviderResolver implementation found!");
    }
}
