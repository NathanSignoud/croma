package com.tsan.chromaynk.exceptions;

public class VariableTypeMismatchException extends Exception{
    
    String msg;

    public VariableTypeMismatchException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }

}
