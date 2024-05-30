package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class ForExpression extends NonTerminalExpression{

    private Expression variable;
    private Assignable condition;
    private Expression action;
    private Expression body;

    public ForExpression(Expression variable, Assignable condition, Expression action, Expression body)
    {
        this.variable = variable;
        this.condition = condition;
        this.action = action;
        this.body = body;
    }

    public void execute(Context context) throws InterruptedException {
        variable.execute(context);
        while(condition.getValue(context).isTrue())
        {
            if(context.returned()) return;
            body.execute(context);
            action.execute(context);
        }
    }

}
