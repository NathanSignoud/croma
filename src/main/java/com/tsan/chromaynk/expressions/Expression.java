package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;

public abstract class Expression {

    abstract public void execute(Context context) throws InterruptedException;
    
}
