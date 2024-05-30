package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Variable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FunctionExpression extends NonTerminalExpression implements Assignable{

    private List<Assignable> arguments;
    private Context functionContext;
    private String name;

    public FunctionExpression(String name, List<Assignable> arguments)
    {
        this.name = name;
        this.arguments = arguments;
    } 

    public void execute(Context context) throws InterruptedException {
        Map<String, Variable> variable = new HashMap<String, Variable>();

        for(int i = 0 ; i < arguments.size() ; i++)
        {
            variable.put("arg"+i, arguments.get(i).getValue(context));
        }
        variable.put("argc", new Num(arguments.size()));

        functionContext = context.deriveContext(variable);
        
        context.getFunction(name).execute(functionContext);

        //System.out.println("arg0" + " " + arguments.get(0).getValue(context) + " returned " +functionContext.getReturnValue());
    }

    public Variable getValue(Context context) throws InterruptedException {
        execute(context);
        return functionContext.getReturnValue();
    }
    
}
