/*
  File Name: warmup.flex
  JFlex specification for the SGML language
*/
   
import java.util.ArrayList;

%%
   
%class Lexer
%type Token
%line
%column
    
%eofval{
  //If the tag still has stacks, report this and print all tags
  if (tagStack.size() > 0){
      System.err.println("Error: tag stack items remain due to unbalanced stack. Listed below:");
        for (String item : tagStack) {
            System.err.println(item);
        }
  }
  //Else, the stack is balaned and nothing should be printed.
  return null;
%eofval};

%{
  //ArrayList to hold all of the currently open tags
  private static ArrayList<String> tagStack = new ArrayList<String>();

  //Used to hold all "good tags", for easier comparison in the open tag.
  private static ArrayList<String> goodTags = new ArrayList<String>(){
      {
          add("DOC");
          add("TEXT");
          add("DATE");
          add("DOCNO");
          add("HEADLINE");
          add("LENGTH");
      }
  };

  //Flag to know whether or not output should be suppressed.
  private Integer isRelevant = new Integer(-1);
%};


/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or form feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]
   

//digits defines all numbers, and positiveNegative holds the two symbols needed for the generalized number case
digit = [0-9]
positiveNegative = [+-]

//number is built here to make the code below more simple.
number = {positiveNegative}?({digit}+|{digit}+\.{digit}+|{digit}\.{digit}+)

//all identifiers are letters and numbers, so together the will create an identifier
lettersAndNumbers = [a-zA-Z0-9]
//an identifier
identifier = {lettersAndNumbers}+

//These regex expressions handle hyphenated words, apostrophized words, and the combination words.
hyphenWord = ({identifier}-)+{identifier}
aposWord = ({identifier}')+{identifier}
hyphenAndApos = ({identifier}[-\'])+{identifier}+
%%

/*
Operations are handled below. For the most part, they are just calls to the regex above. If it is a direct call,
then no explanation is given as to what it does, as it has already been explained above.
*/


//Open tag regex, and holds the Java code to handle the tag
"<" {WhiteSpace}*{identifier}{WhiteSpace}*(({identifier}|{WhiteSpace})* \= ({identifier}|{WhiteSpace}| [\"])* )* ">" {
    String tempString = new String(yytext()).toUpperCase();
    tempString = tempString.replace('<',' ');
    tempString = tempString.replace('>',' ').trim();
    tagStack.add(0,tempString);
    if (goodTags.contains(tempString.split(" ")[0]) && isRelevant == -1){
        return new Token(Token.OPENTAG, tempString.split(" ")[0], yyline, yycolumn);
    } else if (tempString.equals("P") && isRelevant == -1){
        return new Token(Token.OPENTAG, tempString.split(" ")[0], yyline, yycolumn);
    }
    else{
        if (isRelevant == -1){
            isRelevant = tagStack.size();
        }
    }
}

//Close tag regex, and holds the code to handle the tag
"</"{WhiteSpace}*{identifier}+{WhiteSpace}*">"
{
    String tempString = new String(yytext()).toUpperCase();
    tempString = tempString.replace("</"," ");
    tempString = tempString.replace('>',' ').trim();
    if ((tagStack.get(0).split(" ")[0].trim()).equals(tempString)){
        tagStack.remove(0);
    }else{
        System.err.println("Error: unbalanced tags in file: expected \"" + tempString + "\" and got \"" + tagStack.get(0).split(" ")[0].trim() + "\"");
    }
    if (isRelevant != -1 && tagStack.size() < isRelevant){
        isRelevant = -1;
    }else if(isRelevant == -1){
        return new Token(Token.CLOSETAG, tempString, yyline, yycolumn);
    }
}

{hyphenWord}       { if (isRelevant == -1)return new Token(Token.HYPHENWORD, yytext(), yyline, yycolumn); }
{aposWord}         { if (isRelevant == -1)return new Token(Token.APOSWORD, yytext(), yyline, yycolumn); }
{hyphenAndApos}    { if (isRelevant == -1)return new Token(Token.APOSWORD, yytext(), yyline, yycolumn); }
{number}           { if (isRelevant == -1)return new Token(Token.NUM, yytext(), yyline, yycolumn); }
{identifier}       { if (isRelevant == -1)return new Token(Token.ID, yytext(), yyline, yycolumn); }
{WhiteSpace}+      { /* skip whitespace */ }
"{"[^\}]*"}"       { /* skip comments */ }
.                  { if (isRelevant == -1)return new Token(Token.PUNCTUATION, yytext(), yyline, yycolumn); }
