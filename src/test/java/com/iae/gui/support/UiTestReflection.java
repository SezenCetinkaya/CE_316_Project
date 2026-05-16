package com.iae.gui.support;

public final class UiTestReflection {

    private UiTestReflection() {}

    public static <T> T getField(Object controller, String fieldName, Class<T> type) {
        try {
            var field = controller.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(controller));
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Could not read field: " + fieldName, ex);
        }
    }
}
