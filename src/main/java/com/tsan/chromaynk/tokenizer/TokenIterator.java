package com.tsan.chromaynk.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class TokenIterator {
    
    private List<Token> list;
    private int index;

    public TokenIterator(List<Token> list)
    {
        this.index = 0;
        if(list == null) list = new ArrayList<Token>();
        this.list = list;
    }

    public boolean ended()
    {
        return(index >= list.size());
    }

    public Token next()
    {
        index++;
        try{
            return list.get(index);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    public Token current()
    {
        try{
            return list.get(index);
        }
        catch(IndexOutOfBoundsException e){
            return new Token(Token.tokenType.EOF, "");
        }
    }

    public Token previous()
    {
        index--;
        try{
            return list.get(index);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

}
