package com.tsan.chromaynk.parser;

import com.tsan.chromaynk.datatypes.Num;
import com.tsan.chromaynk.datatypes.Variable;
import com.tsan.chromaynk.exceptions.ParsingFailedException;
import com.tsan.chromaynk.exceptions.SyntaxErrorException;
import com.tsan.chromaynk.exceptions.UnexistingOperatorException;
import com.tsan.chromaynk.expressions.*;
import com.tsan.chromaynk.Context;
import com.tsan.chromaynk.tokenizer.TokenIterator;
import com.tsan.chromaynk.tokenizer.Token.tokenType;
import com.tsan.chromaynk.Operation;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.tsan.chromaynk.tokenizer.Token;

public class Parser {


    public Parser(){}

    /**
     * This is the main parse method, it redirects tokens to corresponding methods<br>
     * It is used to parse bodies
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     */
    public Expression parse(TokenIterator iterator) throws ParsingFailedException
    {
        System.out.println("Parsing: body");
        List<Expression> list = new ArrayList<Expression>(); 
        
        try
        {
            while(iterator.current() != null)
            {
                //System.out.println("switch");
                switch (iterator.current().getType()) {

                    case WORD:
                        list.add(parseWord(iterator));
                        break;
                    
                    case NAME:
                        list.add(parseName(iterator));
                        break;
                
                    case RBRACK:
                        //iterator.next();
                        return new ListExpression(list);
                    
                    case EOF:
                        return new ListExpression(list);

                    default:
                        break;
                }

            }
        }
        catch(SyntaxErrorException e){
            e.display();
            throw new ParsingFailedException("parsing failed");
        }

        return new ListExpression(list);
    }

    /**
     * 
     * @param iterator
     * @return
     */
    public Expression parsePrint(TokenIterator iterator)
    {
        System.out.println("Parsing: print");
        iterator.next();
        Expression print = new PrintExpression(iterator.next().getValue());
        iterator.next();
        iterator.next();
        iterator.next();
        return print;
    }

    /**
     * This method redirects token to corresponding method<br>
     * Parses value assignments, function calls, etc...
     * 
     * @param iterator
     * @return
     * @throws SyntaxErrorException
     * @throws ParsingFailedException
     */
    public Expression parseName(TokenIterator iterator) throws SyntaxErrorException, ParsingFailedException
    {
        System.out.println("Parsing: name");
        String name = iterator.current().getValue();

        try
        {
            switch (name) {

                case "PRINT":
                    return parsePrint(iterator);
                
                case "DEL":
                    return parseVariableDeletion(iterator);
                
                case "CURSOR":
                    return parseCursorCreation(iterator);
                
                case "REMOVE":
                    return parseCursorDeletion(iterator);
                
                case "SELECT":
                    return parseCursorSelection(iterator);

                default:
                    // keeping bcs i dunno
                    //if(iterator.next().getType() == Token.tokenType.LPAREN) return null;
                    //iterator.previous();

                    if(iterator.next().getType() == Token.tokenType.LPAREN)
                    {
                        iterator.previous();
                        if(isInstruction(name)) return parseInstruction(iterator);
                        return parseFunctionCall(iterator);
                    }
                    iterator.previous();

                    if(iterator.next().getType() == Token.tokenType.OP & iterator.current().getValue().equals("="))
                    {
                        iterator.previous();
                        return parseAssignement(iterator);
                    }
                    throw new SyntaxErrorException("variable name must be followed by operation");
            }
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("name");
        }
        

    }

    /**
     * This method redirects token to corresponding method<br>
     * Parses all keywords
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     */
    public Expression parseWord(TokenIterator iterator) throws ParsingFailedException
    {
        System.out.println("Parsing: word");
        try{

            switch (iterator.current().getValue()) {

                case "NUM":
                case "STR":
                case "BOOL":
                    return parseVariableCreation(iterator);
                
                case "IF":
                    return parseIfBlock(iterator);

                case "WHILE":
                    return parseWhileBlock(iterator);
                
                case "FOR":
                    return parseForExpression(iterator);
                
                case "DEF":
                    return parseFunctionCreation(iterator);
                
                case "RETURN":
                    return parseReturnExpression(iterator);

                default:
                    throw new ParsingFailedException("Parsing error occured during WORD parsing: unnkown word, "+iterator.current().getValue());
            }

        }
        catch(SyntaxErrorException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured during WORD parsing: syntax error");
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured during WORD parsing: parsing failed");
        }
        
    }

    private Expression parseCursorSelection(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: cursor selection");
        iterator.next();
        Assignable id = parseOperation(iterator);
        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in cursor selection: expression must end with ';'");
        iterator.next();
        return new SelectCursorExpression(id);
    }

    private Expression parseCursorDeletion(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: cursor deletion");
        iterator.next();
        Assignable id = parseOperation(iterator);
        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in cursor deletion: expression must end with ';'");
        iterator.next();
        return new CursorDeletionExpression(id);
    }

    private Expression parseCursorCreation(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: cursor creation");
        iterator.next();
        Assignable id = parseOperation(iterator);
        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in cursor creation: expression must end with ';'");
        iterator.next();
        return new CreateCursorExpression(id);
    }

    private Expression parseReturnExpression(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: return expression");
        iterator.next();
        Assignable value = parseOperation(iterator);
        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in return expression: expression must end with ';'");
        iterator.next();
        return new ReturnExpression(value);
    }

    private Expression parseForExpression(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: for loop");
        try{
            if(iterator.next().getType() != Token.tokenType.LPAREN) throw new SyntaxErrorException("Parsing error occured in for expression: valid format is FOR(variable, condition, action)");
            Expression variable = parseVariableCreation(iterator);
            Assignable condition = parseOperation(iterator);
            List<Token> actionTokens = new ArrayList<Token>(); 
            while(iterator.current().getType() != Token.tokenType.SEMICOL){
                actionTokens.add(iterator.current());
                iterator.next();
            }
            System.out.println(iterator.current().getValue());
            if(iterator.next().getType() != Token.tokenType.RPAREN) throw new SyntaxErrorException("Parsing error occured in for expression: valid format is FOR(variable; condition; action;)");
            Expression action = parse(new TokenIterator(actionTokens));
            if(iterator.next().getType() != Token.tokenType.LBRACK) throw new SyntaxErrorException("Parsing error occured in for expression: body must start with bracket");
            Expression body = parse(iterator);
            if(iterator.current().getType() != Token.tokenType.RBRACK) throw new SyntaxErrorException("Parsing error occured in for expression: body must end with bracket");
            iterator.next();
            return new ForExpression(variable, condition, action, body);
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in for expression");
        }
        catch(SyntaxErrorException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in for expression: syntax error");
        }

    }

    /**
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     * @throws SyntaxErrorException
     */
    private FunctionExpression parseFunctionCall(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: function call");
        String name = iterator.current().getValue();

        System.out.println(name);
        if(iterator.next().getType() != Token.tokenType.LPAREN) throw new SyntaxErrorException("Parsing error occured in function call: function parameters must begin with '('");
        iterator.next();
        
        List<Assignable> list = new ArrayList<Assignable>();
        
        if(iterator.current().getType() == Token.tokenType.RPAREN)
        {
            if(iterator.next().getType() == Token.tokenType.SEMICOL){
                iterator.next();
                return new FunctionExpression(name, list);
            }
            iterator.previous();
        }

        while(iterator.current().getType() != Token.tokenType.SEMICOL)
        {
            List<Token> parameter = new ArrayList<Token>();

            while(iterator.current().getType() != Token.tokenType.COMA)
            {
                if(iterator.current().getType() == Token.tokenType.RPAREN)
                {
                    if(iterator.next().getType() == Token.tokenType.SEMICOL)
                    {
                        iterator.previous();
                        break;
                    }
                    iterator.previous();
                }
                parameter.add(iterator.current());
                iterator.next();
            }

            list.add(parseOperation(new TokenIterator(parameter)));
            iterator.next();
        }

        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in function call: statement must end with ';'");

        iterator.next();
        return new FunctionExpression(name, list);
    }

    private boolean isInstruction(String name)
    {
        if(name.equals("FWD") || name.equals("BWD") || name.equals("TURN") || name.equals("MOV")
        || name.equals("POS") || name.equals("HIDE") || name.equals("SHOW") || name.equals("PRESS")
        || name.equals("COLOR") || name.equals("THICK") || name.equals("LOOKAT")) return true;
        return false;
    }

    private Expression parseInstruction(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: instruction");
        String name = iterator.current().getValue();
        boolean percent1 = false, percent2 = false;
        System.out.println(name);
        if(iterator.next().getType() != Token.tokenType.LPAREN) throw new SyntaxErrorException("Parsing error occured in function call: function parameters must begin with '('");
        iterator.next();
        
        List<Assignable> list = new ArrayList<Assignable>();
        
        if(iterator.current().getType() == Token.tokenType.RPAREN)
        {
            if(iterator.next().getType() == Token.tokenType.SEMICOL){
                iterator.next();
                return new InstructionExpression(name, list, percent1, percent2);
            }
            iterator.previous();
        }

        while(iterator.current().getType() != Token.tokenType.SEMICOL)
        {
            List<Token> parameter = new ArrayList<Token>();

            while(iterator.current().getType() != Token.tokenType.COMA)
            {
                if(iterator.current().getType() == Token.tokenType.PERC)
                {
                    if(iterator.next().getType() == Token.tokenType.RPAREN)
                    {
                        percent2 = true;
                    }
                    iterator.previous();
                    if(iterator.next().getType() == Token.tokenType.COMA)
                    {
                        percent1 = true;
                    }
                    iterator.previous();
                }
                if(iterator.current().getType() == Token.tokenType.RPAREN)
                {
                    if(iterator.next().getType() == Token.tokenType.SEMICOL)
                    {
                        iterator.previous();
                        break;
                    }
                    iterator.previous();
                }
                if(iterator.current().getType() != Token.tokenType.PERC) parameter.add(iterator.current());
                iterator.next();
            }

            list.add(parseOperation(new TokenIterator(parameter)));
            iterator.next();
        }

        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in function call: statement must end with ';'");

        iterator.next();
        return new InstructionExpression(name, list, percent1, percent2);
    }

    /**
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     * @throws SyntaxErrorException
     */
    private Expression parseFunctionCreation(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: function creation");
        if(iterator.next().getType() != Token.tokenType.NAME) throw new SyntaxErrorException("Parsing error occured in function creation: DEF must be followed by name");
        String name = iterator.current().getValue();
        if(iterator.next().getType() != Token.tokenType.LBRACK) throw new SyntaxErrorException("Parsing error occured in function creation: function body starts with bracket");
        iterator.next();
        Expression body;
        try{
            body = parse(iterator);
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in function creation: body cannot be parsed");
        }
        if(iterator.current().getType() != Token.tokenType.RBRACK) throw new SyntaxErrorException("Parsing error occured in function creation: function body must end with bracket");
        iterator.next();
        return new FunctionCreateExpression(name, body);
    }

    /**
     * 
     * @param iterator
     * @return
     * @throws SyntaxErrorException
     */
    private Expression parseVariableDeletion(TokenIterator iterator) throws SyntaxErrorException
    {
        System.out.println("Parsing: variable deletion");
        iterator.next(); // (
        
        if(iterator.current().getType() != Token.tokenType.LPAREN) throw new SyntaxErrorException("Parsing error occured in variable deletion: variable name must be between parentheses");
        iterator.next(); // variable
        
        if(iterator.current().getType() != Token.tokenType.NAME) throw new SyntaxErrorException("Parsing error occured in variable deletion: variable name missing");
        Expression node = new DeletionExpression(iterator.current().getValue());
        iterator.next(); // )
        
        if(iterator.current().getType() != Token.tokenType.RPAREN) throw new SyntaxErrorException("Parsing error occured in variable deletion: variable name must be between parentheses");
        iterator.next(); // ;

        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in variable deletion: statement must end with ';'");
        iterator.next();

        return node;
    }

    /**
     * This method parses variable creation processes
     * redo method
     * 
     * @param iterator
     * @return
     * @throws SyntaxErrorException
     * @throws ParsingFailedException
     */
    private Expression parseVariableCreation(TokenIterator iterator) throws SyntaxErrorException, ParsingFailedException
    {
        System.out.println("Parsing: variable creation");
        List<Expression> list = new ArrayList<Expression>();
        String VariableType = iterator.current().getValue();
        if(iterator.next().getType() != Token.tokenType.NAME) throw new SyntaxErrorException(VariableType + "statement must be followed by a variable name");
        String variableName = iterator.current().getValue();

        switch (VariableType) {
            case "STR":
                System.out.println("creating STR expression");
                list.add(new CreateVariableExpression(iterator.current().getValue(), CreateVariableExpression._type.str));
                break;
            
            case "NUM":
                System.out.println("creating NUM expression");
                list.add(new CreateVariableExpression(iterator.current().getValue(), CreateVariableExpression._type.num));
                break;
            
            case "BOOL":
                System.out.println("creating BOOL expression");
                list.add(new CreateVariableExpression(iterator.current().getValue(), CreateVariableExpression._type.bool));
                break;
                
            default:
                break;
        }
        
        if(iterator.next().getType() == Token.tokenType.SEMICOL)
        {
            iterator.next();
            return new ListExpression(list);
        }
        
        if( iterator.current().getType() == Token.tokenType.OP & iterator.current().getValue().equals("=") )
        {
            System.out.println("parsing: variable initialising");
            iterator.next();
            Assignable value;
            try{ value = parseOperation(iterator); }
            catch(ParsingFailedException e){
                e.display();
                throw new ParsingFailedException("Parsing error occured in variable creation");
            }
            catch(SyntaxErrorException e){
                throw new ParsingFailedException("Parsing error occured in variable creation: syntax error");
            }

            if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in variable creation: variable declaration must be followed by ';' to end the statement");
            iterator.next();
            Expression assign = new AssignExpression(variableName, value);
            list.add(assign);
            System.out.println("variable value initialised");
            return new ListExpression(list);
        }

        throw new SyntaxErrorException("Parsing error occured in variable creation: Variable declaration must be followed by ';' or '='");

    }

    /**
     * This method parses value assignment processes
     * 
     * @param iterator
     * @return
     * @throws SyntaxErrorException
     * @throws ParsingFailedException
     */
    private Expression parseAssignement(TokenIterator iterator) throws SyntaxErrorException, ParsingFailedException
    {
        System.out.println("Parsing: value assignement");
        String name = iterator.current().getValue();
        iterator.next(); // token: = sign
        iterator.next(); // start of operation
        Assignable value;

        if(iterator.current().getType() == Token.tokenType.NAME)
        {
            if(iterator.next().getType() == Token.tokenType.LPAREN)
            {
                iterator.previous();
                value = parseFunctionCall(iterator);
                //iterator.next();
                return new AssignExpression(name, value);
            }
            iterator.previous();
        }

        try{ value = parseOperation(iterator); }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in while block");
        }
        catch(SyntaxErrorException e)
        {
            e.display();
            throw new ParsingFailedException("Parsing error occured in while block: syntax error");
        }
        
        if(iterator.current().getType() != Token.tokenType.SEMICOL) throw new SyntaxErrorException("Parsing error occured in value assignment: value must be followed by ';'");
        iterator.next();
        return new AssignExpression(name, value);
    }

    /**
     * This method parses if blocks
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     */
    private Expression parseIfBlock(TokenIterator iterator) throws ParsingFailedException
    {
        try{
            System.out.println("Parsing: if");
            iterator.next();
            Assignable condition = parseOperation(iterator);
            if(iterator.current().getType() != Token.tokenType.LBRACK) throw new ParsingFailedException("Parsing error occured in if block: left bracket must follow condition");
            iterator.next();
            Expression body = parse(iterator);
            if(iterator.current().getType() != Token.tokenType.RBRACK) throw new ParsingFailedException("Parsing error occured in if block: body must end with '{'");
            iterator.next();
            return new IfExpression(condition, body);
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in if block");
        }
        catch(SyntaxErrorException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in if block: syntax error");
        }
        
    }

    /**
     * This method parses while blocks
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     */
    private Expression parseWhileBlock(TokenIterator iterator) throws ParsingFailedException
    {
        try{
            System.out.println("Parsing: while");
            iterator.next();
            Assignable condition = parseOperation(iterator);
            if(iterator.current().getType() != Token.tokenType.LBRACK) throw new ParsingFailedException("Parsing error occured in while block: left bracket must follow condition");
            iterator.next();
            Expression body = parse(iterator);
            if(iterator.current().getType() != Token.tokenType.RBRACK) throw new ParsingFailedException("Parsing error occured in while block: body must end with '{'");
            iterator.next();
            return new WhileExpression(condition, body);
        }
        catch(ParsingFailedException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in while block");
        }
        catch(SyntaxErrorException e){
            e.display();
            throw new ParsingFailedException("Parsing error occured in while block: syntax error");
        }
        
    }

    /**
     * This method is a direct implementation of the Shunting yard algorithm by Dijkstra<br>
     * Once tokens are listed in RPN format, they are parsed
     * 
     * @param iterator
     * @return
     * @throws ParsingFailedException
     * @throws SyntaxErrorException
     */
    public Assignable parseOperation(TokenIterator iterator) throws ParsingFailedException, SyntaxErrorException
    {
        System.out.println("Parsing: operation");
        // list of token types relating to operations
        List<Token.tokenType> operationTokens = Arrays.asList(new Token.tokenType[]{
                Token.tokenType.OP,
                Token.tokenType.NAME,
                Token.tokenType.VALUE,
                Token.tokenType.LPAREN,
                Token.tokenType.RPAREN});
        
        // Map to store precedence(0) and associativity(1), 0 for left and 1 for right
        Map<String, Integer[]> properties = new HashMap<String, Integer[]>();
        Integer[] parentheses = {7,0};
        properties.put("(", parentheses); properties.put(")", parentheses);
        Integer[] not = {6, 1};
        properties.put("!", not);
        Integer[] mult = {5, 0};
        properties.put("*", mult); properties.put("/", mult);
        Integer[] plus = {4, 0};
        properties.put("+", plus); properties.put("-", plus);
        Integer[] ineq = {3, 0};
        properties.put("<", ineq); properties.put("<=", ineq); properties.put(">", ineq); properties.put(">=", ineq);
        Integer[] eq = {2, 0};
        properties.put("==", eq); properties.put("!=", eq);
        Integer[] and = {1, 0};
        properties.put("&", and); properties.put("|", and);

        // this initial part of the algorithm provides the expression in the RPN format
        
        List<Token> queue = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();

        // bug ???
        // and statements do not act lazy in while conditions
        // edit: not a bug, && and & discern in meaning

        // This is a direct implementation of the Shunting yard algorithm
        // For more information, look up the Shunting yard algorithn by Dijkstra
        while(!iterator.ended() && operationTokens.contains(iterator.current().getType()))
        {
            Token current = iterator.current();

            if(current.getType() == Token.tokenType.OP && current.getValue().equals("-") && iterator.next().getType() == Token.tokenType.VALUE)
            {
                System.out.println("test");
                iterator.previous();
                Token previousToken = iterator.previous();
                iterator.next();
                if(previousToken.getType() == Token.tokenType.LPAREN || previousToken.getType() == Token.tokenType.OP)
                {
                    Token value = iterator.next();
                    value.setValue( String.valueOf( -Double.parseDouble(value.getValue()) ) );
                    current = value;
                }
            }

            if(current.getType() == Token.tokenType.VALUE)
                queue.add(current);
            
            else if(current.getType() == Token.tokenType.NAME)
                queue.add(current);
            
            else if(current.getType() == Token.tokenType.OP)
            {
                if(current.getValue().equals("="))
                    throw new SyntaxErrorException("Parsing error occured during operation parsing, '=' operator is not valid");
                
                if(!stack.isEmpty())
                while ( (!stack.isEmpty()) && (!stack.isEmpty() && stack.peek().getType() != Token.tokenType.LPAREN) &&
                        ( properties.get(stack.peek().getValue())[0] > properties.get(current.getValue())[0] ||
                        (properties.get(stack.peek().getValue())[0] == properties.get(current.getValue())[0] && properties.get(current.getValue())[0] == 0) ) )
                {
                    queue.add(stack.pop());
                    if(stack.isEmpty()) break;
                }
                stack.add(current);
            }

            else if(current.getType() == Token.tokenType.LPAREN)
                stack.add(current);
            
            else if(current.getType() == Token.tokenType.RPAREN)
            {
                if(stack.isEmpty()) throw new SyntaxErrorException("Parsing error occured during operation parsing: parentheses are not consistent");
                while(!stack.isEmpty() && stack.peek().getType() != Token.tokenType.LPAREN) {
                    queue.add(stack.pop());
                }
                if(stack.peek().getType() != Token.tokenType.LPAREN | stack.isEmpty())
                    throw new SyntaxErrorException("Parsing error occured during operation parsing: parentheses are not consistent");
                
                stack.pop();
            }

            iterator.next();
            if(iterator.ended()) break;
        }

        while(!stack.isEmpty()) {
            queue.add(stack.pop());
        }

        // the second part of the algorithm builds the tree from the RPN format

        List<Assignable> assignableList = new ArrayList<Assignable>();

        try{
            for(Token t : queue)
            {
                System.out.println(t.getValue());
                if(t.getType() == Token.tokenType.VALUE)
                {
                    assignableList.add(new ValueExpression(new Num( Double.parseDouble(t.getValue()) )));
                    continue;
                }
                if(t.getType() == Token.tokenType.NAME)
                {
                    assignableList.add(new VariableExpression(t.getValue()));
                    continue;
                }
                if(t.getType() == Token.tokenType.OP){
                    assignableList.add(new OperationExpression(null, null, createOp(t.getValue())));
                    continue;
                }
                throw new SyntaxErrorException("Operation cannot be parsed: unrelated token: "+t.getValue());
            }
        }
        catch(UnexistingOperatorException e){
            e.display();
            throw new SyntaxErrorException("Operation cannot be parsed: unnkown operator");
        }
        
        int index = 0;
        while(assignableList.size() > 1)
        {
            if(index >=  assignableList.size()) return null;
            Assignable node = assignableList.get(index);
            if(node instanceof ValueExpression | node instanceof VariableExpression)
            {
                index++;
                continue;
            }
            if(node instanceof OperationExpression)
            {
                if( ((OperationExpression)node).isNotOp() )
                {
                    if(index-1 < 0) throw new SyntaxErrorException("Operation cannot be parsed: no operand for NOT operation");
                    ((OperationExpression)node).setParameter(assignableList.remove(index-1), null);
                    continue;
                }
                if(index-2 < 0) throw new SyntaxErrorException("Operation cannot be parsed: not enogh operands for operation with two parameters");
                ((OperationExpression)node).setParameter(assignableList.remove(index-2), assignableList.remove(index-2));
                index--;
                continue;
            }
            throw new SyntaxErrorException("Operation cannot be parsed");
        }

        return assignableList.get(0);
    }

    /**
     * 
     * @param operator
     * @return
     * @throws UnexistingOperatorException
     */
    public Operation createOp(String operator) throws UnexistingOperatorException
    {
        switch (operator) {
            case "+": return Operation.PLUS;
            case "-": return Operation.MINUS;
            case "*": return Operation.MULT;
            case "/": return Operation.DIV;
            case "&": return Operation.AND;
            case "|": return Operation.OR;
            case "==": return Operation.EQ;
            case "!=": return Operation.NEQ;
            case "!": return Operation.NOT;
            case "<": return Operation.INF;
            case "<=": return Operation.INFEQ;
            case ">": return Operation.SUP;
            case ">=": return Operation.SUPEQ;
            default: throw new UnexistingOperatorException("Parsing error occured during operator treatment, unnkown operator: "+operator);
        }
    }

}