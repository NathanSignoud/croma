package com.tsan.chromaynk.exceptions;

public class UnexistingOperatorException extends Exception{
    
    String msg;

    public UnexistingOperatorException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }

}
