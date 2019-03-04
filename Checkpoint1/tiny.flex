/*
  Created By: Fei Song
  File Name: tiny.flex
  To Build: jflex tiny.flex
  and then after the parser is created
    javac Lexer.java
*/

/* --------------------------Usercode Section------------------------ */

import java_cup.runtime.*;

%%

/* -----------------Options and Declarations Section----------------- */

/*
   The name of the class JFlex will create will be Lexer.
   Will write the code to the file Lexer.java.
*/
%class Lexer

%eofval{
  return null;
%eofval};

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column

/*
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup

/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/
%{
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}


/*
  Macro Declarations

  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.
*/

/* A line terminator is a \r (carriage return), \n (line feed), or \r\n. */
LineTerminator = \r|\n|\r\n

/* White space is a line terminator, space, tab, or form feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]

// number is built here to make the code below more simple.
numberV2 = [0-9]+

// identifier
identifierV2 = [_a-zA-Z][_a-zA-Z0-9]*

comment = \/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+\/

%%
/* ------------------------Lexical Rules Section---------------------- */

/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

//keyword creation

"if"               { System.out.print(yytext());
                        return symbol(sym.IF); }
"else"             { System.out.print(yytext());
						return symbol(sym.ELSE); }
"int"              { System.out.print(yytext());
						return symbol(sym.INT); }
"return"           { System.out.print(yytext());
						return symbol(sym.RETURN); }
"void"             { System.out.print(yytext());
						return symbol(sym.VOID); }
"while"            { System.out.print(yytext());
						return symbol(sym.WHILE); }

// special symbols

"+"                { System.out.print(yytext());
						return symbol(sym.PLUS); }
"-"                { System.out.print(yytext());
						return symbol(sym.MINUS); }
"*"                { System.out.print(yytext());
						return symbol(sym.TIMES); }
"/"                { System.out.print(yytext());
						return symbol(sym.OVER); }
"<"                { System.out.print(yytext());
						return symbol(sym.LT); }
">"                { System.out.print(yytext());
						return symbol(sym.GT); }
"="                { System.out.print(yytext());
						return symbol(sym.EQ); }
";"                { System.out.print(yytext());
						return symbol(sym.SEMI); }
"("                { System.out.print(yytext());
						return symbol(sym.LPAREN); }
")"                { System.out.print(yytext());
						return symbol(sym.RPAREN); }
"<="               { System.out.print(yytext());
						return symbol(sym.LTE); }
">="               { System.out.print(yytext());
						return symbol(sym.GTE); }
"=="               { System.out.print(yytext());
						return symbol(sym.COMPARE); }
"!="               { System.out.print(yytext());
						return symbol(sym.NE); }
","                { System.out.print(yytext());
						return symbol(sym.COMMA); }
"["                { System.out.print(yytext());
						return symbol(sym.SQLEFT); }
"]"                { System.out.print(yytext());
						return symbol(sym.SQRIGHT); }
"{"                { System.out.print(yytext());
						return symbol(sym.SQUIGLEFT); }
"}"                { System.out.print(yytext());
						return symbol(sym.SQUIGRIGHT); }

//Other token definitions

{numberV2}         { System.out.print(yytext());
						return symbol(sym.NUM, yytext()); }
{identifierV2}     { System.out.print(yytext());
						return symbol(sym.ID, yytext()); }
{WhiteSpace}+      { /* skip whitespace */ System.out.print(yytext()); }
{comment}          { System.out.println("COMMENT FOUND, SKIPPING"); /*skip comments*/ }
.                  { System.err.println("ERROR: Unrecognized character \'" + yytext() +"\' on line " + yyline);
                        return symbol(sym.ERROR); }
