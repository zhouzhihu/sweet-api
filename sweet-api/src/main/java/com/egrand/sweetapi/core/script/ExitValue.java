package com.egrand.sweetapi.core.script;

public class ExitValue {
    private Object[] values;

    public ExitValue() {
        this(new Object[0]);
    }

    public ExitValue(Object[] values) {
        this.values = values;
    }

    public Object[] getValues() {
        return this.values;
    }

    public int getLength() {
        return this.values.length;
    }
}
