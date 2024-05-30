package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class WhileExpression extends NonTerminalExpression{
    
    private Assignable condition;
    private Expression body;

    public WhileExpression(Assignable condition, Expression body)
    {
        this.condition = condition;
        this.body = body;
    }

    public void execute(Context context) throws InterruptedException {
        while(condition.getValue(context).isTrue())
        {
            if(context.returned()) return;
            body.execute(context);
        }
    }

}
