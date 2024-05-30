package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class FunctionCreateExpression extends TerminalExpression
{

    private String name;
    private Expression body;

    public FunctionCreateExpression(String name, Expression body)
    {
        this.name = name;
        this.body = body;
    }
    
    public void execute(Context context)
    {
        //if(body == null) exception
        context.addFunction(name, body);
    }

}
