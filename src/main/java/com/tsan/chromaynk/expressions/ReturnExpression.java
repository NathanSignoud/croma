package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class ReturnExpression extends TerminalExpression{
    
    private Assignable value;

    public ReturnExpression(Assignable value)
    {
        this.value = value;
    }

    public void execute(Context context) throws InterruptedException {
        context.setReturnValue(value.getValue(context));
        context.setReturned();
    }

}
