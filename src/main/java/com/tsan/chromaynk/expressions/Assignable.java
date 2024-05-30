package com.tsan.chromaynk.expressions;

import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.datatypes.Variable;

public interface Assignable {
    
    abstract public Variable getValue(Context context) throws InterruptedException;

}
