package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.*;

import com.example.demo1.InterfaceController;

public class CreateCursorExpression extends TerminalExpression{

    private Assignable id;

    public CreateCursorExpression(Assignable id)
    {
        this.id = id;
    }

    public void execute(Context context) throws InterruptedException {
        Variable var = id.getValue(context);
        Integer id;
        if(var instanceof Str)
        {
            System.out.println("Failed to create cursor: String cannot be converted to integer");
            return;
        }
        if(var instanceof Bool)
        {
            System.out.println("Failed to create cursor: cannot initialise cursor id from bool");
            return;
        }
        if(var instanceof Num)
        {
            id = ((Num)var).getValue().intValue();
            if(context.addCursor(id, new Cursor()))
            {
                System.out.println("Cursor created: "+id);
            }
            return;
        }
    }
    
}
