package parser;
import java_cup.runtime.*;

import java.io.IOException;
import java.io.StringReader;

%%

%class Scanner
%public
%unicode
%cup

%{  
  void print(String s){
  	System.out.println(s);
  }
  
  private Symbol sym(int sym) {
    return new Symbol(sym);
  }

  private Symbol sym(int sym, Object val) {
    return new Symbol(sym, val);
  }
  
   public static void main(String[] args) {
	String s = "";

    Scanner scanner = new Scanner(new StringReader(s));
    while ( !scanner.zzAtEOF )
		try {
			scanner.next_token();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
%}

WhiteSpace     = [ \t\f\r\n]


%%
/*comment*/

"/*" ~"*/"						{/*ignore, comments don't nest*/}

/* keywords */

"-" | "minus"					{return sym(sym.MINUS);}
"." | "∙" | "dot"				{return sym(sym.DOT);}
"timelt" | "<"					{return sym(sym.LT);}
"agentlt" | "⊴"					{return sym(sym.AGENTLT);}
"="	| "≅"						{return sym(sym.EQ);}
":"								{return sym(sym.COLON);}
"square" | "boxdot" | "⊡"		{return sym(sym.SQUARE);}
"not" | "¬"						{return sym(sym.NOT);}
"iff" | "<=>"					{return sym(sym.IFF);}
"implies" | "=>"				{return sym(sym.IMPLIES);}
"and" | "⋀"	| "&&"				{return sym(sym.AND);}
"or" | "⋁"	| "||"				{return sym(sym.OR);}
"true" | "⊤"					{return sym(sym.TRUE);}
"false" | "⊥"					{return sym(sym.FALSE);}
"always" | "G"					{return sym(sym.ALWAYS);}
"eventually" | "F"				{return sym(sym.EVENTUALLY);}
"next" | "X"					{return sym(sym.NEXT);}
"until" | "U"					{return sym(sym.UNTIL);}
"("	| "{"						{return sym(sym.LPAREN);}
")"	| "}"						{return sym(sym.RPAREN);}
"["								{return sym(sym.LBRACK);}
"]"								{return sym(sym.RBRACK);}


[[:jletter:]--[XGFU]] [:jletterdigit:]* 			{return sym(sym.IDENTIFIER, yytext()); /*identifiers mustn't start with a letter which represents a time operator*/}
[\"\'][[:jletter:]] [:jletterdigit:]* [\"\']		{return sym(sym.IDENTIFIER, yytext());}

{WhiteSpace}					{/*ignore*/}
[^]								{ throw new Error("Lexical error: " + yytext()); }	