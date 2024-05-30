package com.tsan.chromaynk.datatypes;

public class Str extends Variable{

    private String value;

    public Str(){}

    public Str(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals(Object o)
    {
        if( o == null | !(o instanceof Str) ) return false;
        return ( this.value.equals( ((Str)o).value ) );
    }

    @Override
    public void setValue(Variable value)
    {
        if(value instanceof Str)
            this.value = ((Str)value).getValue();
        
        // throw exception !!
    }

    @Override
    public boolean isTrue()
    {
        return false;
    }
}
