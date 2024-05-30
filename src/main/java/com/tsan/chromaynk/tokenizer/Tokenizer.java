    package com.tsan.chromaynk.tokenizer;

    import java.util.List;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.util.Scanner;
    import java.util.ArrayList;

    public class Tokenizer {

        private List<Token> tokens;

        public Tokenizer()
        {
            this.tokens = new ArrayList<Token>(); 
        }

        /*
         * returns token iterator for parsing
         */
        public TokenIterator getTokenList()
        {
            return new TokenIterator(tokens);
        }
        
        /*
         * 
         */
        public void display()
        {
            for(Token t : tokens) System.out.println(t);
        }

        /**
         * 
         * @param file
         */
        public void tokenize(String file)
        {
            try
            {
                Scanner scanner = new Scanner(new File(file));
                int line = 0;
                while (scanner.hasNextLine())
                {
                    String s = scanner.nextLine();
                    line = line+1;
                    for(int i = 0 ; i < s.length() ; i++)
                    {
                        char c = s.charAt(i);

                        // skip comment line
                        if(s.length() > i+1)
                        {
                            if(c == '/' && s.charAt(i+1) == '/') break;
                        }

                        // skip blankspace
                        if(c == ' ' || c == '\t') continue;

                        if(Character.isDigit(c))
                        {
                            int j = i;
                            boolean pointCount = false;
                            while (j+1 < s.length()) {
                                if(Character.isDigit(s.charAt(j+1)))
                                {
                                    j++;
                                    continue;
                                }
                                else if(s.charAt(j+1) == '.')
                                {
                                    if(pointCount)
                                    {
                                        System.out.println("\nTokenization error on line " + line + " : numerical value not valid");
                                        // throw exception
                                        System.exit(-1);
                                    }
                                    pointCount = true;
                                    j++;
                                }
                                else
                                {
                                    tokens.add( new Token(Token.tokenType.VALUE, s.substring(i, j+1)) );
                                    i = j;
                                    break;
                                }
                            }
                            if(j+1 == s.length())
                            {
                                tokens.add( new Token(Token.tokenType.VALUE, s.substring(i, j+1)) );
                                i = j;
                            }
                        }

                        // Variable names may only begin with an alphabetical character
                        // but may contain numerical characters
                        if(Character.isAlphabetic(c))
                        {
                            int j = i;
                            while(j+1 < s.length())
                            {
                                if(Character.isAlphabetic(s.charAt(j+1)) | Character.isDigit(s.charAt(j+1)))
                                {
                                    j++;
                                    continue;
                                }
                                else
                                {
                                    String sub = s.substring(i, j+1);
                                    if(isWord(sub)) tokens.add( new Token(Token.tokenType.WORD, sub));
                                    else tokens.add( new Token(Token.tokenType.NAME, sub));
                                    i = j;
                                    break;
                                }
                            }
                        }
                        
                        if(c == '=' || c == '!' || c == '<' || c == '>')
                        {
                            if(i+1 < s.length() && s.charAt(i+1) == '=')
                            {
                                this.tokens.add(new Token(Token.tokenType.OP, String.valueOf(c) + "="));
                                i++;
                            }
                            else
                                this.tokens.add(new Token(Token.tokenType.OP, String.valueOf(c) ));
                            
                            continue;
                        }

                        if(c == '%')
                        {
                            this.tokens.add(new Token(Token.tokenType.PERC, String.valueOf(c) ));
                            continue;
                        }

                        if(evalSymbol(c)) continue;

                    }
                        
                }

                scanner.close();

            }
            catch(FileNotFoundException e)
            {
                System.out.println("Error: File not found");
            }
        }

        /*
        * Checks if string in parameter is a reserved word
        */
        private boolean isWord(String s)
        {
            if (s.equals("DEF") || s.equals("NUM") || s.equals("BOOL") || s.equals("STR") || s.equals("IF") || s.equals("FOR") || s.equals("WHILE") || s.equals("MIMIC") || s.equals("MIRROR") || s.equals("RETURN")) {
                //tokens.add(new Token(Token._token.WORD, s));
                return true;
            }
            return false;
        }

        /*
        * Looks for syntaxic symbols and arithmetic operations
        * Creates Token in case of a match
        */
        private boolean evalSymbol(char c)
        {
            switch (c) {
                case '{':
                    this.tokens.add(new Token(Token.tokenType.LBRACK, "{"));
                    return true;
                case '}':
                    this.tokens.add(new Token(Token.tokenType.RBRACK, "}"));
                    return true;
                case '(':
                    this.tokens.add(new Token(Token.tokenType.LPAREN, "("));
                    return true;
                case ')':
                    this.tokens.add(new Token(Token.tokenType.RPAREN, ")"));
                    return true;
                case '"':
                    this.tokens.add(new Token(Token.tokenType.QUOTE, "'"));
                    return true;
                case ';':
                    this.tokens.add(new Token(Token.tokenType.SEMICOL, ";"));
                    return true;
                case ',':
                    this.tokens.add(new Token(Token.tokenType.COMA, ","));
                    return true;
                case '+':
                case '-':
                case '*':
                case '/':
                case '&':
                case '|':
                case '!':
                    this.tokens.add(new Token(Token.tokenType.OP, String.valueOf(c)));
                    return true;
            
                default:
                    return false;
            }
        }

    }
