package flex;
import java_cup.runtime.*;
import cup.sym;
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
	"let"			{return new Symbol(sym.LET);}
	"in"			{return new Symbol(sym.IN);}
	"if"			{return new Symbol(sym.IF);}
	"then"			{return new Symbol(sym.THEN);}
	"else"			{return new Symbol(sym.ELSE);}
	"="				{return new Symbol(sym.ASSIGN);}
	"("				{return new Symbol(sym.PARANTHESIS_OPEN);}
	")"				{return new Symbol(sym.PARANTHESIS_CLOSE);}
	"{"				{return new Symbol(sym.BRACE_OPEN);}
	"}"				{return new Symbol(sym.BRACE_CLOSE);}
	","				{return new Symbol(sym.COMMA);}
	";"				{return new Symbol(sym.SEMICOLON);}
	
	/* Operations */
	"+"				{return new Symbol(sym.OP_PLUS);}
	"-"				{return new Symbol(sym.OP_MINUS);}
	"*"				{return new Symbol(sym.OP_MUL);}
	"/"				{return new Symbol(sym.OP_DIV);}
	"=="			{return new Symbol(sym.OP_EQ);}
	"!="			{return new Symbol(sym.OP_NEQ);}
	">"				{return new Symbol(sym.OP_GT);}
	"<"				{return new Symbol(sym.OP_LT);}
	  
	/* Others */
	{Identifier}	{return new Symbol(sym.ID, yytext());}	
	{Digit}       	{return new Symbol(sym.CONST, new Integer(yytext()));}
	{WhiteSpace}  	{/* do nothing */}
}

/* Sonst liegt ein Fehler vor: */ 
[^]  { throw new Error("Illegal character <" + yytext() + ">"); }