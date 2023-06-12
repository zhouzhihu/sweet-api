package com.egrand.sweetapi.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class BooleanUtils {
    public static boolean isTrue(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (Boolean)object;
        } else if (object instanceof CharSequence) {
            return ((CharSequence)object).length() != 0;
        } else if (object instanceof Number) {
            return ((Number)object).doubleValue() != 0.0D;
        } else if (object instanceof Collection) {
            return !((Collection)object).isEmpty();
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) > 0;
        } else if (object instanceof Map) {
            return !((Map)object).isEmpty();
        } else {
            return true;
        }
    }
}
