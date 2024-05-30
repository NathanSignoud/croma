package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class DeletionExpression extends TerminalExpression{

    private String name;

    public DeletionExpression(String name)
    {
        this.name = name;
    }

    public void execute(Context context)
    {
        context.deleteVariable(name);
    }
    
}
