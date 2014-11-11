/* The Lexer class contains a lexical analyzer that returns Tokens
 *  from the input file on each call to getNextTokens. The class
 *  also contains functions that adjust the spacing based on the
 *  type and context of the Tokens.
 */
package com.hemen.CMSC335.Project1;

import java.io.*;
import java.util.ArrayList;

//Token Definitions
enum Token {PARTY, CREATURE, TREASURE, ARTIFACT,
    STRING, INT, DOUBLE, NONE, NOT_FOUND, END_OF_FILE}

class Lexer {
    
    private ArrayList<String> string = null;
    private String line = "";
    private BufferedReader bufferedReader;
    private Token currentToken, lastToken;
    private String currentLexeme, lastLexeme;
  
    // Constructor initializes private data members and opens the input
    //   file.
    public Lexer(File file) throws IOException {
        //file = new BufferedReader(new FileReader(fileName + ".txt"));
        bufferedReader = new BufferedReader(new FileReader(file));
        line = bufferedReader.readLine();
        string = new ArrayList<String>();
        
        currentLexeme = "";
        lastToken = Token.NONE;
    }
   
    // Closes input file
    public void close() throws IOException {
        bufferedReader.close();
    }
   
   //  getNextToken returns the next token in the input file and 
   //    displays the previous token. Comment and preprocessor tokens
   //    are skipped.
    public Token getNextToken()
    {   
        if (lastToken != Token.NONE)
        {
            currentToken = lastToken;
            lastToken = Token.NONE;
            return currentToken;
        }
        
        lastLexeme = currentLexeme;
   
        try {
           currentLexeme = nextString();
           //System.out.println(currentLexeme);
        } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
    
        // Return end of file token if there are no more lines
        if(line == null) {
            return Token.END_OF_FILE;
        }
        
        // Find out what it is, and return the token for it
        currentToken = testToken(currentLexeme);
        
        return currentToken;
    }
   
    //  Puts back the last token that was gotten.
   
    public void putLastToken() {
        lastToken = currentToken;
    }
   
    //  Returns the lexeme corresponding to the last token.
    public String getLastLexeme() {
        return lastLexeme;
    }
    
    // Returns the lexeme corresponding to the current token.
    //     This is a convenience method for debugging.
    public String getCurrentLexeme() {
        return currentLexeme;
    }
   
   // Returns the next character in the input buffer.
    private String nextString() throws IOException {
        if(line == null) //end of file
            return null;
    
        // String array list is empty, add strings from line
        if(string.size() == 0) { 
                // Get lines until end or one is not a comment
                while(line.equals("") || line.charAt(0) == '/') {
                    line = bufferedReader.readLine();
                    
                    if(line == null)
                        break;
                }
                
                // End of the file
                if(line == null) {
                    return null;
                }
                
                // Add the line's components to the string array
                for(String s : line.split(":")) {
                    string.add(s.trim());
            }
        }
        
        // Return the first usable string
        return string.remove(0);
    }
   
    // testToken will return the token type if it is a token. 
    //  Otherwise,it returns STRING.
    private Token testToken(String lexeme) {
        switch (lexeme.charAt(0)) {
            case 'p':
                if(lexeme.equals("p"))
                    return Token.PARTY;
                else
                    return Token.STRING;
            case 'c':
                if(lexeme.equals("c"))
                    return Token.CREATURE;
                else
                    return Token.STRING;
            case 't':
                if(lexeme.equals("t"))
                    return Token.TREASURE;
                else
                    return Token.STRING;
            case 'a':
                if(lexeme.equals("a"))
                    return Token.ARTIFACT;
                else
                    return Token.STRING;
            default:
                if(Character.isDigit(lexeme.charAt(0))) {
                    if(isInteger(lexeme))
                        return Token.INT;
                
                    if(isDouble(lexeme))
                        return Token.DOUBLE;
                }
                return Token.STRING;
        }
    }
    
    // isInteger checks to make sure that the String parameter
    //     number is an integer, returns true if it is and
    //     false if the parameter is not an integer.
    private boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // isDouble checks to make sure that the String parameter
    //     number is an double, returns true if it is and
    //     false if the parameter is not a double.
    private boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return isInteger(number); //it might still be an integer, which works
        }
    }
    
    // ignoreLineRemainder skips the remaining tokens on the current line
    //     and gets the next line. 
    public void ignoreLineRemainder() {
       try {
           line = bufferedReader.readLine();
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }

       // Remove what is left in the string array list
       string.clear();
    }
    
    // This method will show the next token that has not been
    //     parsed without changing the last or current token.
    public Token peek() {
        if(string.size() == 0)
            return currentToken;
        else
            return Token.NONE;
    }
}
