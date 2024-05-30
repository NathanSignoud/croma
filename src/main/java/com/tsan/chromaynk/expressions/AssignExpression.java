package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Bool;
import com.tsan.chromaynk.datatypes.Str;
import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Variable;

public class AssignExpression extends NonTerminalExpression{
    
    /*
     * name of variable and value to be assigned
     */
    private String variableName;
    private Assignable value;

    public AssignExpression(String variableName, Assignable value)
    {
        this.variableName = variableName;
        this.value = value;
    }

    public void execute(Context context) throws InterruptedException {
        Variable var = context.getVariable(variableName);
        if(var == null)
        {
            System.out.println("Warning: failed to retrieve variable in assign expression");
            System.out.println("Warning: assignment aborted");
            return;
        }

        Variable assign = value.getValue(context);
        if(assign == null)
        {
            System.out.println("Warning: failed to retrieve value in assign expression");
            System.out.println("Warning: assignment aborted");
            return;
        }

        if(var instanceof Bool)
        {
            if(assign instanceof Str){
                System.out.println("Warning: BOOL <- STR");
                System.out.println("Warning: assignment aborted");
                return;
            }
            var.setValue( new Bool(assign.isTrue()) );
        }

        if(var instanceof Num)
        {
            if(assign instanceof Str){
                System.out.println("Warning: BOOL <- STR");
                System.out.println("Warning: assignment aborted");
                return;
            }
            var.setValue(assign);
        }
    }

}
