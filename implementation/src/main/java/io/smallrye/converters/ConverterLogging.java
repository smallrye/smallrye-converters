package io.smallrye.converters;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "SRCNV", length = 5)
public interface ConverterLogging extends BasicLogger {
    ConverterLogging log = Logger.getMessageLogger(ConverterLogging.class, ConverterLogging.class.getPackage().getName());

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 1000, value = "Unable to get context classloader instance")
    void failedToRetrieveClassloader(@Cause Throwable cause);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 1001, value = "Unable to get declared constructor for class %s with arguments %s")
    void failedToRetrieveDeclaredConstructor(@Cause Throwable cause, String clazz, String paramTypes);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 1002, value = "Unable to set accessible flag on %s")
    void failedToSetAccessible(@Cause Throwable cause, String accessibleObject);
}
