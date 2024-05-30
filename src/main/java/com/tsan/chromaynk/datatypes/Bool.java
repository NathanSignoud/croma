package com.tsan.chromaynk.datatypes;

public class Bool extends Variable{
    
    private boolean value;

    public Bool(){}

    public Bool(boolean value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if( o == null | !(o instanceof Bool) ) return false;
        System.out.println("bool test");
        return (this.value == ((Bool)o).value);
    }

    private boolean getValue()
    {
        return this.value;
    }

    @Override
    public void setValue(Variable value)
    {
        this.value = value.isTrue();
    }

    @Override
    public boolean isTrue()
    {
        return value;
    }

}
