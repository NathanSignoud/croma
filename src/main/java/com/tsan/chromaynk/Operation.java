package com.tsan.chromaynk;

import com.tsan.chromaynk.datatypes.Bool;
import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Str;
import com.tsan.chromaynk.datatypes.Variable;
import com.tsan.chromaynk.exceptions.DoubleDivisionByZeroException;
import com.tsan.chromaynk.exceptions.VariableMissingException;
import com.tsan.chromaynk.exceptions.VariableTypeMismatchException;

public enum Operation {

    PLUS("+"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Addition: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) throw new VariableTypeMismatchException("Addition: "+x+" "+y);
            return new Num( ((Num)x).getValue() + ((Num)y).getValue() );
        }
    },
    MINUS("-"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Substraction: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) throw new VariableTypeMismatchException("Substraction: "+x+" "+y);
            return new Num( ((Num)x).getValue() - ((Num)y).getValue() );
        }
    },
    MULT("*"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Multiplication: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) throw new VariableTypeMismatchException("Multiplication: "+x+" "+y);
            return new Num( ((Num)x).getValue() * ((Num)y).getValue() );
        }
    },
    DIV("/"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException, DoubleDivisionByZeroException
        {
            if(x == null | y == null) throw new VariableMissingException("Division: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) throw new VariableTypeMismatchException("Division: "+x+" "+y);
            Double val = ((Num)y).getValue();
            if(Math.abs(val) < 0.00001) throw new DoubleDivisionByZeroException("runtime error: division by zero: "+x+" "+y);
            return new Num( ((Num)x).getValue() / val );
        }
    },
    EQ("=="){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            //System.out.println("eq op: " + x + " " + y);
            if(x == null | y == null) throw new VariableMissingException("Equals operator: "+x+" "+y);
            return new Bool(x.equals(y));
            //return new Bool( Math.abs(((Num)x).getValue() - ((Num)y).getValue()) < 0.00001 );
        }
    },
    NEQ("!="){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Not equals operator: "+x+" "+y);
            return new Bool( !x.equals(y) );
        }
    },
    INF("<"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Inferior operator: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) return null;
            return new Bool( ((Num)x).getValue() < ((Num)y).getValue() );
        }
    },
    INFEQ("<="){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Inf or equal operator: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) return null;
            return new Bool( ((Num)x).getValue() <= ((Num)y).getValue() );
        }
    },
    SUP(">"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Superior operator: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) return null;
            return new Bool( ((Num)x).getValue() > ((Num)y).getValue() );
        }
    },
    SUPEQ(">="){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("Sup or equal operator: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) return null;
            return new Bool( ((Num)x).getValue() >= ((Num)y).getValue() );
        }
    },
    AND("&"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null | y == null) throw new VariableMissingException("And operator: "+x+" "+y);
            if(!(x instanceof Num & y instanceof Num)) return null;
            return new Bool( x.isTrue() & y.isTrue() );
        }
    },
    OR("|"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            //System.out.println("or operation: " + x + " " + y);
            if(x == null | y == null) throw new VariableMissingException("Or operator: "+x+" "+y);
            if((x instanceof Str | y instanceof Str)) throw new VariableTypeMismatchException("Or operator: "+x+" "+y);
            return new Bool( x.isTrue() | y.isTrue() );
        }
    },
    NOT("!"){
        @Override
        public Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException
        {
            if(x == null) throw new VariableMissingException("Not operator: "+x);
            return new Bool( !x.isTrue() );
        }
    };

    private String op;

    private Operation(String op)
    {

    }

    public abstract Variable eval(Variable x, Variable y) throws VariableTypeMismatchException, VariableMissingException, DoubleDivisionByZeroException;
    
}
