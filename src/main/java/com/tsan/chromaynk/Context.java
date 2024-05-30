package com.tsan.chromaynk;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tsan.chromaynk.datatypes.Variable;
import com.tsan.chromaynk.expressions.Expression;
import com.example.demo1.InterfaceController;
import com.tsan.chromaynk.datatypes.Cursor;

public class Context {

    private Map<String, Variable> variable;
    private Map<Integer, Cursor> cursor;
    private Map<String, Expression> function;

    private Variable returnValue = null;
    private boolean returned = false;

    private Cursor mainCursor;

    private InterfaceController controller;

    public Context(InterfaceController controller)
    {
        this.returnValue = null;
        this.returned = false;
        this.controller = controller;
        this.variable = new HashMap<String, Variable>();
        this.cursor = new HashMap<Integer, Cursor>();
        this.function = new HashMap<String, Expression>();
    }

    public Context(Map<String, Variable> variable)
    {
        this.returnValue = null;
        this.returned = false;
        this.variable = variable;
        this.cursor = new HashMap<Integer, Cursor>();
        this.function = new HashMap<String, Expression>();
    }

    public Context deriveContext(Map<String, Variable> variable)
    {
        Context c = new Context(controller);
        c.returnValue = null;
        c.returned = false;
        c.variable = variable;
        c.cursor = this.cursor;
        c.function = this.function;
        return c;
    }

    public InterfaceController getController()
    {
        return controller;
    }

    public Cursor getMainCursor()
    {
        return mainCursor;
    }

    public boolean selectCursor(int id)
    {
        Cursor c = getCursor(id);
        if(c == null) return false;
        mainCursor = c;
        return true;
    }

    public boolean addCursor(int id, Cursor c)
    {
        if(cursor.keySet().contains(id)) return false;
        cursor.put(id, c);
        return true;
    }

    public Cursor getCursor(int id)
    {
        if(!cursor.keySet().contains(id)) return null;
        return cursor.get(id);
    }

    public boolean removeCursor(Integer id)
    {
        if(!cursor.keySet().contains(id)) return false;
        Cursor c = cursor.remove(id);
        if(c.equals(mainCursor)) mainCursor = null;
        return true;
    }

    public Expression getFunction(String name)
    {
        if(!function.keySet().contains(name)) return null;
        return function.get(name);
    }

    public boolean returned()
    {
        return returned;
    }

    public void setReturned()
    {
        returned = true;
    }

    public void setReturnValue(Variable value)
    {
        returnValue = value;
    }

    public Variable getReturnValue()
    {
        return returnValue;
    }

    public boolean addFunction(String name, Expression body)
    {
        if(function.keySet().contains(name))
            return false;

        function.put(name, body);
        return true;
    }

    // throw var exists exception ?
    public boolean addVariable(String name, Variable var)
    {
        if(variable.keySet().contains(name))
            return false;

        variable.put(name, var);
        return true;
    }

    public boolean deleteVariable(String name)
    {
        if(!variable.keySet().contains(name)) return false;
        
        variable.remove(name);
        return true;
    }

    public Variable getVariable(String variableName)
    {
        if(!variable.keySet().contains(variableName)) return null;
        return variable.get(variableName);
    }

    // private



}
