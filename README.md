## Compiler for Imperative Language

#### Technological Stack

- Target platform: JVM
- Implementation language/tool : Java, bison-based parser
- Lexer: Jflex
- Target language: Jasmin assembler

#### Team Members

- Anna Rylova
- Aymen Daassi
- Sofya Ivanova

#### WorkFlow

So, how it works is (in terminal):
- `jflex lexical_spec.jflex` generates lexer by the name "Yylex.java"
- `java java_cup.MainDrawTree` parser.cup generates "parser.java" and "sym.java" *
- ``javac *.java``
generates the rest of the files
- ``java Main input.txt`` tests how the parser interprets input (draws an AST)
