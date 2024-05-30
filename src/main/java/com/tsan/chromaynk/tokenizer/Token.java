package com.tsan.chromaynk.tokenizer;

public class Token {

    private String value;
    private tokenType token;

    public Token(tokenType token, String value)
    {
        this.token = token;
        this.value = value;
    }

    public Token(tokenType token)
    {
        this.token = token;
        if(token == tokenType.LPAREN) this.value = "(";
        else if(token == tokenType.RPAREN) this.value = ")";
        else this.value = null;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    public tokenType getType()
    {
        return this.token;
    }

    @Override
    public String toString()
    {
        String s = "";

        switch (token) {
            case WORD:
                s+="WORD";
                break;
            case NAME:
                s+="NAME";
                break;
            case VALUE:
                s+="VALUE";
                break;
            case OP:
                s+="OP";
                break;
            case LPAREN:
                s+="LPAREN";
                break;
            case RPAREN:
                s+="RPAREN";
                break;
            case LBRACK:
                s+="LBRACK";
                break;
            case RBRACK:
                s+="RBRACK";
                break;
            case SEMICOL:
                s+="SEMICOL";
                break;
            case QUOTE:
                s+="QUOTE";
                break;
            case COMA:
                s+="COMA";
                break;
            case EOF:
                s+="EOF";
                break;
            case PERC:
                s+="PERC";
                break;
        
            default:
                break;
        }

        if(value == null) return s;

        return s + " \t" + value;
    }

    public static enum tokenType
    {
        WORD, NAME, VALUE, OP, LPAREN, RPAREN, LBRACK, RBRACK, SEMICOL, QUOTE, COMA, EOF, PERC
    }
    
}
