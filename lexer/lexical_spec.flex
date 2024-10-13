/* JFlex definition for the custom language */
%%
%public
%class Lexer
%unicode
%type String

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
"var"        { return "VAR"; }
"integer"    { return "INTEGER_TYPE"; }
"boolean"    { return "BOOLEAN_TYPE"; }
"real"       { return "REAL_TYPE"; }
"array"      { return "ARRAY"; }
"record"     { return "RECORD"; }
"true"       { return "TRUE"; }
"false"      { return "FALSE"; }
"is"         { return "IS"; }
"if"         { return "IF"; }
"then"       { return "THEN"; }
"else"       { return "ELSE"; }
"end"        { return "END"; }
"for"        { return "FOR"; }
"in"         { return "IN"; }
"loop"       { return "LOOP"; }
"while"      { return "WHILE"; }
"routine"    { return "ROUTINE"; }
"return"     { return "RETURN"; }

/* Operators */
"+"          { return "PLUS"; }
"-"          { return "MINUS"; }
"*"          { return "MULTIPLY"; }
"/"          { return "DIVIDE"; }
":="         { return "ASSIGN"; }
"="          { return "EQUAL"; }
">"          { return "GREATER_THAN"; }
"<"          { return "LESS_THAN"; }
"and"        { return "AND"; }
"or"         { return "OR"; }
"xor"        { return "XOR"; }
"not"        { return "NOT"; }


/* Delimiters */
"("          { return "LPAREN"; }
")"          { return "RPAREN"; }
"["          { return "LBRACKET"; }
"]"          { return "RBRACKET"; }
","          { return "COMMA"; }
"."          { return "DOT"; }
".."         { return "RANGE"; }
":"          { return "COLON"; }
";"          { return "SEMICOLON"; }

/* Identifiers */
{IDENTIFIER} { return "IDENTIFIER, yytext()"; }

/* Numbers */
{NUMBER}     { return "NUMBER, Integer.parseInt(yytext())"; }
{REAL}       { return "REAL, Double.parseDouble(yytext())"; }