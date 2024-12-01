import java_cup.runtime.*;

/* JFlex definition for the custom language */
%%

%unicode
%cup

%{
  /* User code section for imports, variable declarations, etc. */
%}

/* Definitions */
DIGIT       = [0-9]
LETTER      = [a-zA-Z]
IDENTIFIER  = {LETTER}({LETTER}|{DIGIT}|_)*
NUMBER      = {DIGIT}+
REAL        = {DIGIT}+ "." {DIGIT}*
WHITESPACE  = [ \t\n\r]+
COMMENT     = "//" [^\n]*


/* Regular expression rules */
%%

/* Skip whitespaces */
{WHITESPACE} { /* Ignore whitespaces */ }

/* Comments */
{COMMENT}    { /* Ignore comments */ }

/* Keywords */
"var"        { System.out.println("VAR"); return new Symbol(sym.VAR); }
"integer"    { System.out.println("INTEGER_TYPE"); return new Symbol(sym.INTEGER_TYPE); }
"boolean"    { System.out.println("BOOLEAN_TYPE"); return new Symbol(sym.BOOLEAN_TYPE); }
"real"       { System.out.println("REAL_TYPE"); return new Symbol(sym.REAL_TYPE); }
"array"      { System.out.println("ARRAY"); return new Symbol(sym.ARRAY); }
"record"     { System.out.println("RECORD"); return new Symbol(sym.RECORD); }
"true"       { System.out.println("TRUE"); return new Symbol(sym.TRUE); }
"false"      { System.out.println("FALSE"); return new Symbol(sym.FALSE); }
"is"         { System.out.println("IS"); return new Symbol(sym.IS); }
"if"         { System.out.println("IF"); return new Symbol(sym.IF); }
"then"       { System.out.println("THEN"); return new Symbol(sym.THEN); }
"else"       { System.out.println("ELSE"); return new Symbol(sym.ELSE); }
"end"        { System.out.println("END"); return new Symbol(sym.END); }
"for"        { System.out.println("FOR"); return new Symbol(sym.FOR); }
"in"         { System.out.println("IN"); return new Symbol(sym.IN); }
"loop"       { System.out.println("LOOP"); return new Symbol(sym.LOOP); }
"while"      { System.out.println("WHILE"); return new Symbol(sym.WHILE); }
"routine"    { System.out.println("ROUTINE"); return new Symbol(sym.ROUTINE); }
"return"     { System.out.println("RETURN"); return new Symbol(sym.RETURN); }
"print"      { System.out.println("PRINT"); return new Symbol(sym.PRINT); }

/* Operators */
"+"          { System.out.println("PLUS"); return new Symbol(sym.PLUS); }
"-"          { System.out.println("MINUS"); return new Symbol(sym.MINUS); }
"*"          { System.out.println("MULTIPLY"); return new Symbol(sym.MULTIPLY); }
"/"          { System.out.println("DIVIDE"); return new Symbol(sym.DIVIDE); }
":="         { System.out.println("ASSIGN"); return new Symbol(sym.ASSIGN); }
"="          { System.out.println("EQUAL"); return new Symbol(sym.EQUAL); }
">"          { System.out.println("GREATER_THAN"); return new Symbol(sym.GREATER_THAN); }
"<"          { System.out.println("LESS_THAN"); return new Symbol(sym.LESS_THAN); }
"<="         { System.out.println("LESS_THAN_OR_EQUAL"); return new Symbol(sym.LESS_THAN_OR_EQUAL); }
">="         { System.out.println("GREATER_THAN_OR_EQUAL"); return new Symbol(sym.GREATER_THAN_OR_EQUAL); }
"and"        { System.out.println("AND"); return new Symbol(sym.AND); }
"or"         { System.out.println("OR"); return new Symbol(sym.OR); }
"xor"        { System.out.println("XOR"); return new Symbol(sym.XOR); }
"not"        { System.out.println("NOT"); return new Symbol(sym.NOT); }

/* Delimiters */
"("          { System.out.println("OPEN PAREN"); return new Symbol(sym.LPAREN); }
")"          { System.out.println("CLOSE PAREN"); return new Symbol(sym.RPAREN); }
"["          { System.out.println("OPEN BRACKET"); return new Symbol(sym.LBRACKET); }
"]"          { System.out.println("CLOSE BRACKET"); return new Symbol(sym.RBRACKET); }
","          { System.out.println("COMMA"); return new Symbol(sym.COMMA); }
"."          { System.out.println("DOT"); return new Symbol(sym.DOT); }
".."         { System.out.println("RANGE"); return new Symbol(sym.RANGE); }
":"          { System.out.println("COLON"); return new Symbol(sym.COLON); }
";"          { System.out.println("SEMICOLON"); return new Symbol(sym.SEMICOLON); }

/* Identifiers */
{IDENTIFIER} { System.out.println("IDENTIFIER: " + yytext()); return new Symbol(sym.IDENTIFIER, yytext()); }

/* Numbers */
{NUMBER}     { System.out.println("NUMBER: " + yytext()); return new Symbol(sym.NUMBER, Integer.parseInt(yytext())); }
{REAL}       { System.out.println("REAL: " + yytext()); return new Symbol(sym.REAL, Double.parseDouble(yytext())); }