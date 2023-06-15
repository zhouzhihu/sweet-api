package com.egrand.sweetapi.web.utils;


import com.egrand.sweetapi.web.exception.OpenException;

import java.time.LocalDateTime;

/**
 * 断言帮助类
 */
public class Assert {
    public static void fail(int code, String message) {
        throw new OpenException(code, message);
    }

    public static void fail() {
        fail(-9, "参数验证异常");
    }

    public static void fail(String message) {
        if (message == null || "".equals(message)) {
            message = "参数验证异常";
        }

        fail(-9, message);
    }

    public static void isTrue(boolean condition, String exceptionMessage) {
        if (!condition) {
            fail(exceptionMessage);
        }

    }

    public static void isTrue(boolean condition) {
        if (!condition) {
            fail();
        }

    }

    public static void isFalse(boolean condition, String exceptionMessage) {
        if (condition) {
            fail(exceptionMessage);
        }
    }

    public static void isFalse(boolean condition, int code, String exceptionMessage){
        if (condition) {
            fail(code, exceptionMessage);
        }
    }

    public static void notNull(Object object) {
        if (object == null) {
            fail();
        }

    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            fail(message);
        }

    }

    public static void notEmpty(String value, String exceptionMsgs) {
        if (value == null || value.isEmpty()) {
            fail(exceptionMsgs);
        }

    }

    public static void notEmpty(String value) {
        if (value == null || value.isEmpty()) {
            fail();
        }

    }

    public static void equals(String expected, String actual, String exceptionMsgs) {
        if (expected != null || actual != null) {
            if (expected == null || !expected.equals(actual)) {
                fail(exceptionMsgs);
            }
        }
    }

    public static void equals(Object expected, Object actual, String exceptionMsgs) {
        if (expected != null || actual != null) {
            if (expected == null || !expected.equals(actual)) {
                fail(exceptionMsgs);
            }
        }
    }

    public static void gt(LocalDateTime expected, LocalDateTime actual, String exceptionMsgs) {
        if (expected == null || actual == null) {
            fail(exceptionMsgs);
        }

        if (expected.isAfter(actual)) {
            fail(exceptionMsgs);
        }

    }
}
