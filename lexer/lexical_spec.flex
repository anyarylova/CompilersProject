 /* JFlex definition for the custom language */
%%
%public
%class Lexer
%unicode
%cup

%{
  /* User code section for imports, variable declarations, etc. */
%}

/* Definitions */
DIGIT       = [0-9]
LETTER      = [a-zA-Z]
IDENTIFIER  = {LETTER}({LETTER}|{DIGIT})*
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
"var"        { return sym(VAR); }
"integer"    { return sym(INTEGER_TYPE); }
"boolean"    { return sym(BOOLEAN_TYPE); }
"real"       { return sym(REAL_TYPE); }
"array"      { return sym(ARRAY); }
"record"     { return sym(RECORD); }
"true"       { return sym(TRUE); }
"false"      { return sym(FALSE); }
"is"         { return sym(IS); }
"if"         { return sym(IF); }
"then"       { return sym(THEN); }
"else"       { return sym(ELSE); }
"end"        { return sym(END); }
"for"        { return sym(FOR); }
"in"         { return sym(IN); }
"loop"       { return sym(LOOP); }
"while"      { return sym(WHILE); }
"routine"    { return sym(ROUTINE); }
"return"     { return sym(RETURN); }

/* Operators */
"+"          { return sym(PLUS); }
"-"          { return sym(MINUS); }
"*"          { return sym(MULTIPLY); }
"/"          { return sym(DIVIDE); }
":="         { return sym(ASSIGN); }
"="          { return sym(EQUAL); }
">"          { return sym(GREATER_THAN); }
"<"          { return sym(LESS_THAN); }
"and"        { return sym(AND); }

/* Delimiters */
"("          { return sym(LPAREN); }
")"          { return sym(RPAREN); }
"["          { return sym(LBRACKET); }
"]"          { return sym(RBRACKET); }
","          { return sym(COMMA); }
"."          { return sym(DOT); }

/* Identifiers */
{IDENTIFIER} { return sym(IDENTIFIER, yytext()); }

/* Numbers */
{NUMBER}     { return sym(NUMBER, Integer.parseInt(yytext())); }
{REAL}       { return sym(REAL, Double.parseDouble(yytext())); }
