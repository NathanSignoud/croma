package com.tsan.chromaynk.exceptions;

public class VariableMissingException extends Exception{
 
    String msg;

    public VariableMissingException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }

}
