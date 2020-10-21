package io.smallrye.converters;

import java.io.InvalidObjectException;
import java.util.regex.Pattern;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import io.smallrye.converters.api.Converter;

@MessageBundle(projectCode = "SRCNV", length = 5)
interface ConverterMessages {
    ConverterMessages msg = Messages.getBundle(ConverterMessages.class);

    @Message(id = 1, value = "Converter class %s not found")
    IllegalArgumentException classConverterNotFound(@Cause Throwable cause, String className);

    @Message(id = 2, value = "Host, %s, not found")
    IllegalArgumentException unknownHost(@Cause Throwable cause, String host);

    @Message(id = 3, value = "%s can not be converted to a Character")
    IllegalArgumentException failedCharacterConversion(String value);

    @Message(id = 4, value = "Converter %s must be parameterized with a single type")
    IllegalStateException singleTypeConverter(String className);

    @Message(id = 5, value = "%s is not an array type")
    IllegalArgumentException notArrayType(String arrayType);

    @Message(id = 6, value = "Value does not match pattern %s (value was \"%s\")")
    IllegalArgumentException valueNotMatchPattern(Pattern pattern, String value);

    @Message(id = 7, value = "Value must not be less than %s (value was \"%s\")")
    IllegalArgumentException lessThanMinimumValue(Object minimum, String value);

    @Message(id = 8, value = "Value must not be less than or equal to %s (value was \"%s\")")
    IllegalArgumentException lessThanEqualToMinimumValue(Object minimum, String value);

    @Message(id = 9, value = "Value must not be greater than %s (value was \"%s\")")
    IllegalArgumentException greaterThanMaximumValue(Object maximum, String value);

    @Message(id = 10, value = "Value must not be greater than or equal to %s (value was \"%s\")")
    IllegalArgumentException greaterThanEqualToMaximumValue(Object maximum, String value);

    @Message(id = 11, value = "Array type being converted is unknown")
    IllegalArgumentException unknownArrayType();

    @Message(id = 12, value = "Unknown converter ID: %s")
    InvalidObjectException unknownConverterId(int id);

    @Message(id = 13, value = "Failed to convert value with static method")
    IllegalArgumentException staticMethodConverterFailure(@Cause Throwable cause);

    @Message(id = 14, value = "Failed to create new instance from Converter constructor")
    IllegalArgumentException constructorConverterFailure(@Cause Throwable cause);

    @Message(id = 15, value = "No Converter registered for %s")
    IllegalArgumentException noRegisteredConverter(Class<?> type);

    @Message(id = 16, value = "Can not add converter %s that is not parameterized with a type")
    IllegalStateException unableToAddConverter(Converter<?> converter);

    @Message(id = 17, value = "Expected an integer value, got \"%s\"")
    NumberFormatException integerExpected(String value);

    @Message(id = 18, value = "Expected a long value, got \"%s\"")
    NumberFormatException longExpected(String value);

    @Message(id = 19, value = "Expected a double value, got \"%s\"")
    NumberFormatException doubleExpected(String value);

    @Message(id = 20, value = "Expected a float value, got \"%s\"")
    NumberFormatException floatExpected(String value);
}