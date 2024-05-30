package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public class IfExpression extends NonTerminalExpression{
    
    /*
     * condition must be met for the body to be executed
     * if the condition cannot be checked due to a parsing error,
     * a run time error is met
     */
    private Assignable condition;
    private Expression body;

    public IfExpression(Assignable condition, Expression body)
    {
        this.condition = condition;
        this.body = body;
    }

    public void execute(Context context) throws InterruptedException {
        if(condition == null | body == null)
        {
            System.out.println("condition or body null in if statement");
            //return;
        }
        if(condition.getValue(context) == null){
            System.out.println("condition couldnt return value in if statement");
            return;
        }
        if(condition.getValue(context).isTrue())
        {
            body.execute(context);
            return;
        }
    }

}
