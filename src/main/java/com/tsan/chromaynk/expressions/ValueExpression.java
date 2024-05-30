package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Variable;

public class ValueExpression extends TerminalExpression implements Assignable{

    private Variable value;

    public ValueExpression(Variable variable)
    {
        this.value = variable;
    }

    public void execute(Context context)
    {
        return;
    }
    
    
    @Override
    public Variable getValue(Context context)
    {
        return value;
    }

}
