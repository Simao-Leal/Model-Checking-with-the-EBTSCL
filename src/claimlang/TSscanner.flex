/*
 * The EventTransitionSystem class used to be called ActionTransitionSystem.
 * This is why there are many references to action_statements, action_state, action_transitions, etc.
 * The classes in the AST are also called ActionStateNode, etc. It would be nice to change these all
 * to EventStateNode, etc.
 */

package claimlang;
import java_cup.runtime.*;

import java.io.IOException;
import java.io.StringReader;
 
%%

%class TSScanner
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
  
%}

WhiteSpace     = [ \t\f\r\n]


%%
/*comment*/

"/*" ~"*/"						{/*ignore. Note: comments don't nest*/}
"//" .* \R?						{/*ignore there is a ? because EOF is not end of line*/}
"\"" ~"\""						{return sym(sym.tFORMULALITERAL, yytext().substring(1, yytext().length() - 1));}


/* keywords */

"TransitionSystem"				{return sym(sym.tTRANSITIONSYSTEM);}
"EventTransitionSystem"		    {return sym(sym.tACTIONTRANSITIONSYSTEM); /* I admit, this is not very elegant*/}
"State"							{return sym(sym.tSTATE);}
"Initial"						{return sym(sym.tINITIAL);}
"TimeOrder"						{return sym(sym.tTIMEORDER);}
"TrustOrder"					{return sym(sym.tTRUSTORDER);}
"Event"							{return sym(sym.tEVENT);}
"Formula"						{return sym(sym.tFORMULA);}
"Check"							{return sym(sym.tCHECK);}
"Satisfiable" 					{return sym(sym.tSATISFIABLE);}
"Satisfies"						{return sym(sym.tSATISFIES);}
"Valid"							{return sym(sym.tVALID);}


"->"							{return sym(sym.tARROW);}
"-" | "Minus"					{return sym(sym.tMINUS);}
"." | "∙" | "Dot"				{return sym(sym.tDOT);}
"Timelt" | "<"					{return sym(sym.tLT);}
"Agentlt" | "⊴"					{return sym(sym.tAGENTLT);}
"="	| "≅"						{return sym(sym.tEQ);}
":"								{return sym(sym.tCOLON);}
";"								{return sym(sym.tSEMICOLON);}
"*"								{return sym(sym.tPRODUCT);}

"("								{return sym(sym.tLPAREN);}
")"								{return sym(sym.tRPAREN);}
"["								{return sym(sym.tLBRACK);}
"]"								{return sym(sym.tRBRACK);}
"{"								{return sym(sym.tLCURLY);}
"}"								{return sym(sym.tRCURLY);}



[:jletter:] [:jletterdigit:]* 	{return sym(sym.tIDENTIFIER, yytext()); }

{WhiteSpace}					{/*ignore*/}
[^]								{ throw new Error("Lexical error: " + yytext()); }