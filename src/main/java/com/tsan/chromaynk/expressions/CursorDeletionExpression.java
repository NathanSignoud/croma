package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Bool;
import com.tsan.chromaynk.datatypes.Cursor;
import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Str;
import com.tsan.chromaynk.datatypes.Variable;

public class CursorDeletionExpression extends TerminalExpression{

    private Assignable id;

    public CursorDeletionExpression(Assignable id)
    {
        this.id = id;
    }

    public void execute(Context context) throws InterruptedException {
        Variable var = id.getValue(context);
        Integer id;
        if(var instanceof Str)
        {
            System.out.println("Failed to remove cursor: String cannot be converted to integer");
            return;
        }
        if(var instanceof Bool)
        {
            System.out.println("Failed to remove cursor: cannot initialise cursor id from bool");
            return;
        }
        if(var instanceof Num)
        {
            id = ((Num)var).getValue().intValue();
            if(context.removeCursor(id))
            {
                System.out.println("Cursor removed: "+id);
            }
            return;
        }
        System.out.println("ERROR in cursor deletion expression");
    }
    
}
