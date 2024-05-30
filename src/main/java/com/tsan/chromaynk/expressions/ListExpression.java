package com.tsan.chromaynk.expressions;

import java.util.ArrayList;
import java.util.List;
import com.tsan.chromaynk.Context;

public class ListExpression extends NonTerminalExpression{

    private List<Expression> list;

    public ListExpression(){
        list = new ArrayList<Expression>();
    }

    public ListExpression(List<Expression> l)
    {
        list = l;
    }

    public void execute(Context context) throws InterruptedException {
        for(Expression exp : list){
            if(context.returned()) return;
            exp.execute(context);
        }
    }
    
}
