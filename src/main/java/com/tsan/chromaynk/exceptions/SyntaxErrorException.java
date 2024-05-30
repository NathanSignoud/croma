package com.tsan.chromaynk.exceptions;

public class SyntaxErrorException extends Exception{

    String msg;

    public SyntaxErrorException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }
    
}
