package de.niecklikescode.turing.api.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// TODO: Add an obfuscation map reader to automate the process of reading obfuscated fields till then use bspkrs' "MCP Mapping Viewer"
public class ReflectionUtils {

    public static void invokePrivate(Object object, String name, String obfuscated) throws NoSuchMethodException {
        Method method;

        // Once the code runs outside the IDE, the code will be obfuscated, so we have to account for both the method names
        try {
            method = object.getClass().getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            method = object.getClass().getDeclaredMethod(obfuscated);
        }
        method.setAccessible(true);

        try {
            method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object accessField(Object object, String name, String obfuscated) throws IllegalAccessException {
        // Once the code runs outside the IDE, the code will be obfuscated, so we have to account for both the field names
        try {
            return FieldUtils.readField(object, name, true);
        } catch (IllegalAccessException e) {
            return FieldUtils.readField(object, obfuscated, true);
        }

    }

    public static void setField(Object target, Object value, String name, String obfuscated) throws IllegalAccessException {
        try {
            FieldUtils.writeField(target, name, value, true);
        } catch (IllegalAccessException e) {
            FieldUtils.writeField(target, obfuscated, value, true);
        }
    }

}
