/*
 * Nico Feld - 1169233
 */

package flex;
import java_cup.runtime.*;
import cup.*;
%%

/* Name des erzeugten Lexers */
%class Lexer

/* Erzeugter Lexer soll Zeilen- und Spaltennummer in den Variablen yyline 
   und yycolumn bereitstellen */ 
%line
%column
%public

/* Erzeugter Lexer soll mit JCUP kompatibel sein */
%cup

%eofval{
  return new java_cup.runtime.Symbol(Symbols.EOF);
%eofval}

%{
   private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
   private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Macro Declarations */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Digit          = 0 | [1-9][0-9]*
Identifier     = [A-Za-z_][A-Za-z0-9_]*

%%

/* Lexical Rules */
<YYINITIAL> {
	/* Symbols */
	"let"			{return new Symbol(Symbols.LET);}
	"in"			{return new Symbol(Symbols.IN);}
	"if"			{return new Symbol(Symbols.IF);}
	"then"			{return new Symbol(Symbols.THEN);}
	"else"			{return new Symbol(Symbols.ELSE);}
	"do"            {return new Symbol(Symbols.DO);}
	"while"         {return new Symbol(Symbols.WHILE);}
	"="				{return new Symbol(Symbols.ASSIGN);}
	"("				{return new Symbol(Symbols.PARANTHESIS_OPEN);}
	")"				{return new Symbol(Symbols.PARANTHESIS_CLOSE);}
	"{"				{return new Symbol(Symbols.BRACE_OPEN);}
	"}"				{return new Symbol(Symbols.BRACE_CLOSE);}
	","				{return new Symbol(Symbols.COMMA);}
	";"				{return new Symbol(Symbols.SEMICOLON);}
	
	/* Operations */
	"+"				{return new Symbol(Symbols.OP_ADD);}
	"-"				{return new Symbol(Symbols.OP_SUB);}
	"*"				{return new Symbol(Symbols.OP_MULT);}
	"/"				{return new Symbol(Symbols.OP_DIV);}
	"=="			{return new Symbol(Symbols.OP_EQ);}
	"!="			{return new Symbol(Symbols.OP_NEQ);}
	">"				{return new Symbol(Symbols.OP_GT);}
	"<"				{return new Symbol(Symbols.OP_LT);}
	"=>"			{return new Symbol(Symbols.OP_GTE);}
	"=<"			{return new Symbol(Symbols.OP_LTE);}
	"&&"            {return new Symbol(Symbols.OP_AND);}
	"||"            {return new Symbol(Symbols.OP_OR);}

	/* Others */
	"true"          {return new Symbol(Symbols.BOOL,true);}
    "false"         {return new Symbol(Symbols.BOOL,false);}
	{Identifier}	{return new Symbol(Symbols.ID, yytext());}
	{Digit}       	{return new Symbol(Symbols.CONST, new Integer(yytext()));}
	{WhiteSpace}  	{/* do nothing */}
}

/* Sonst liegt ein Fehler vor: */ 
[^]  { throw new Error("Illegal character <" + yytext() + ">"); }