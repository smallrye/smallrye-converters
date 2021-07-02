package io.smallrye.converters;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.smallrye.converters.api.Converters;
import io.smallrye.converters.api.ConvertersProviderResolver;

public class SmallRyeConvertersProviderResolver extends ConvertersProviderResolver {
    private final Map<ClassLoader, Converters> convertersForClassLoader = new ConcurrentHashMap<>();

    static final ClassLoader SYSTEM_CL;

    static {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            SYSTEM_CL = AccessController
                    .doPrivileged(
                            (PrivilegedAction<ClassLoader>) SmallRyeConvertersProviderResolver::calculateSystemClassLoader);
        } else {
            SYSTEM_CL = calculateSystemClassLoader();
        }
    }

    @Override
    public Converters getConverters() {
        return getConverters(getContextClassLoader());
    }

    @Override
    public Converters getConverters(ClassLoader classLoader) {
        final ClassLoader realClassLoader = getRealClassLoader(classLoader);
        Converters converters = convertersForClassLoader.get(realClassLoader);
        if (converters == null) {
            synchronized (convertersForClassLoader) {
                converters = convertersForClassLoader.get(realClassLoader);
                if (converters == null) {
                    converters = new SmallRyeConvertersBuilder().build();
                    convertersForClassLoader.put(realClassLoader, converters);
                }
            }
        }
        return converters;
    }

    private static ClassLoader calculateSystemClassLoader() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl == null) {
            // non-null ref that delegates to the system
            cl = new ClassLoader(null) {
            };
        }
        return cl;
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
                ClassLoader tccl = null;
                try {
                    tccl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                    // TODO - log
                }
                return tccl;
            });
        }
    }

    private static ClassLoader getRealClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = SYSTEM_CL;
        }
        return classLoader;
    }
}
