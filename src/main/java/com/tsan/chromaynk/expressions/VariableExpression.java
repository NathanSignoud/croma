package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Variable;

public class VariableExpression extends TerminalExpression implements Assignable{
    
    private String name;

    public VariableExpression(String name)
    {
        this.name = name;
    }

    public Variable getVariable(Context context)
    {
        return context.getVariable(name);
    }

    public Variable getValue(Context context)
    {
        return context.getVariable(name);
    }

    public void execute(Context context)
    {
        return;
    }



}
