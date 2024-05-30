package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.Operation;
import com.tsan.chromaynk.datatypes.Variable;
import com.tsan.chromaynk.exceptions.DoubleDivisionByZeroException;
import com.tsan.chromaynk.exceptions.VariableMissingException;
import com.tsan.chromaynk.exceptions.VariableTypeMismatchException;

public class OperationExpression extends NonTerminalExpression implements Assignable{

    private Assignable x, y;
    private Operation op;
    
    public OperationExpression(Assignable x, Assignable y, Operation op){
        this.x = x;
        this.y = y;
        this. op = op;
    }

    public boolean isNotOp()
    {
        return (op == Operation.NOT);
    }

    public void setParameter(Assignable x, Assignable y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public Variable getValue(Context context)
    {
        try 
        {
            // if x or y null throw exception
            if(y == null) return op.eval(x.getValue(context), null);
            Variable ret = op.eval(x.getValue(context), y.getValue(context));
            return ret;
        }
        catch(DoubleDivisionByZeroException e)
        {
            System.out.println("return null");
            e.display();
        }
        catch(VariableMissingException e)
        {
            System.out.println("return null");
            e.display();
        }
        catch(VariableTypeMismatchException e)
        {
            System.out.println("return null");
            e.display();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void execute(Context context)
    {
        return;
    }

}
