package com.tsan.chromaynk.exceptions;

public class ParsingFailedException extends Exception{
    
    String msg;

    public ParsingFailedException(String msg)
    {
        this.msg = msg;
    }

    public void display()
    {
        System.out.println(msg);
    }

}
