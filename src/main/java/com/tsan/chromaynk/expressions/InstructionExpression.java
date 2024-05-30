package com.tsan.chromaynk.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Variable;

public class InstructionExpression extends TerminalExpression{

    private List<Assignable> arguments;
    private String name;
    private boolean percent1, percent2;

    public InstructionExpression(String name, List<Assignable> arguments, boolean p1, boolean p2)
    {
        this.name = name;
        this.arguments = arguments;
        this.percent1 = p1;
        this.percent2 = p2;
    }

    public void execute(Context context) throws InterruptedException {
        List<Variable> variable = new ArrayList<Variable>();

        for(int i = 0 ; i < arguments.size() ; i++)
        {
            variable.add(arguments.get(i).getValue(context));
        }

        switch (name) {
            case "FWD":
                if(variable.size() < 1) return;
                context.getController().FWD(context.getMainCursor(), ((Num)variable.get(0)).getValue(), percent2 ? 1 : 0);
                break;
            
            case "BWD":
                if(variable.size() < 1) return;
                context.getController().FWD(context.getMainCursor(), ((Num)variable.get(0)).getValue(), percent2 ? 1 : 0);
                break;
            
            case "MOV":
                if(variable.size() < 2) return;
                context.getController().MOV(context.getMainCursor(), ((Num)variable.get(0)).getValue(), ((Num)variable.get(1)).getValue(), percent1&percent2 ? 1 : 0);
                break;
            
            case "TURN":
                if(variable.size() < 1) return;
                context.getController().TURN(context.getMainCursor(), ((Num)variable.get(0)).getValue());
                break;
            
            case "POS":
                if(variable.size() < 2) return;
                context.getController().POS(context.getMainCursor(), ((Num)variable.get(0)).getValue(), ((Num)variable.get(1)).getValue(), percent1|percent2 ? 1 : 0);
                break;
            
            case "HIDE":
                break;
            
            case "SHOW":
                break;
            
            case "PRESS":
                if(variable.size() < 1) return;
                context.getController().PRESS(context.getMainCursor(), ((Num)variable.get(0)).getValue());
                break;
            
            case "COLOR":
                if(variable.size() < 3) return;
                context.getController().COLORRGB(((Num)variable.get(0)).getValue(), ((Num)variable.get(1)).getValue(), ((Num)variable.get(2)).getValue());
                break;
            
            case "THICK":
                if(variable.size() < 1) return;
                context.getController().THICK(((Num)variable.get(0)).getValue());
                break;
            
            case "LOOKAT":
                if(variable.size() < 2) return;
                context.getController().LOOKAT(context.getMainCursor(), ((Num)variable.get(0)).getValue(), ((Num)variable.get(1)).getValue(), percent1 & percent2 ? 1 : 0);
                break;

            default:
                System.out.println("Unvalid instruction: " + name);
                return;
        }

    }
    
}
