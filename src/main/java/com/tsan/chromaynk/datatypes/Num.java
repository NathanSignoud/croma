package com.tsan.chromaynk.datatypes;

public class Num extends Variable{

    private Double value = 0.0;

    public Num(){}

    public Num(double value)
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
        if( o == null | o instanceof Str ) return false;
        if(o instanceof Bool) return (this.isTrue() & ((Variable)o).isTrue());
        return Math.abs(this.getValue() - ((Num)o).getValue()) < 0.00001;
    }

    public Double getValue()
    {
        return this.value;
    }

    @Override
    public void setValue(Variable value)
    {
        if(value instanceof Num)
        {
            this.value = ((Num)value).getValue();
            return;
        }
            
        
        if(value instanceof Bool)
        {
            if(value.isTrue()) this.value = 1.0;
            else this.value = 0.0;
            return;
        }
        System.out.println("Warning: NUM <- STR");
        System.out.println("Warning: assignment aborted");
    }

    @Override
    public boolean isTrue()
    {
        return (value != 0);
    }
    
}
