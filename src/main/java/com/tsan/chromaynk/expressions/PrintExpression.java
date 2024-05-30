package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Variable;

/*
 * this class is solely used for bug testing
 * it is not to be implemented in the parser
 */
public class PrintExpression extends TerminalExpression{
    
    private String name;

    public PrintExpression(String variableName)
    {
        this.name = variableName;
    }

    /*
     * prints value of variable or error message
     */
    public void execute(Context context)
    {
        Variable var = context.getVariable(name);
        if(var == null) System.out.println("Variable not found: " + name);
        else System.out.println("Variable " + name + " = " + var);
    }

}
