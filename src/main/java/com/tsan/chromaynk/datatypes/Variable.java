package com.tsan.chromaynk.datatypes;

public abstract class Variable{

    public enum type{num, bool, str}

    @Override
    abstract public String toString();

    @Override
    abstract public boolean equals(Object o);

    abstract public void setValue(Variable value);

    // throws error
    abstract public boolean isTrue();

}
