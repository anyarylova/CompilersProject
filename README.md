## Compiler for Imperative Language

#### Technological Stack

- Source language: Imperative
- Implementation language: Java
- Lexer: Jflex - a lexical analyzer generator for Java
- Parser: Java Cup - a parser generator for Java
- Target platform: JVM
- Target language: ASM for code generator to JVM bytecode

#### Team Members

- Anna Rylova
- Aymen Daassi
- Sofya Ivanova

#### Code run
1. `jflex lexical_spec.jflex` generates lexer by the name **Yylex.java**
2. `java java_cup.MainDrawTree parser.cup` - generates **parser.java** and **sym.java**
3. `javac -classpath ".;asm-9.7.1.jar;asm-commons-9.7.1.jar;asm-tree-9.7.1.jar;$CLASSPATH" *.java
` - compiles the rest Java files (in **Git Bash**)
4. `java -classpath ".;asm-9.7.1.jar;asm-commons-9.7.1.jar;asm-tree-9.7.1.jar;$CLASSPATH" Main input.txt` - runs the **Main** file with the test input in **input.txt**
5. `java MainClass` - runs the generated code 
