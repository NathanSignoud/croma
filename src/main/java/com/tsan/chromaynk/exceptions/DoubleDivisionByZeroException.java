package com.tsan.chromaynk.exceptions;

public class DoubleDivisionByZeroException extends Exception{

    String msg;

    public DoubleDivisionByZeroException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }
    
}
