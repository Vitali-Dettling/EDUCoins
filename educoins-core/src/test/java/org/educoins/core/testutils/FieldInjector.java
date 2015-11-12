package org.educoins.core.testutils;

import java.lang.reflect.Field;

/**
 * Injects fields.
 * Created by typus on 11/12/15.
 */
public class FieldInjector {
    public static void setField(Object target, Object source, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = target.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        declaredField.set(target, source);
    }
}
