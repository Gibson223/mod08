lexer grammar Exercise12;

STRING : '"' (~'"' | '"' '"')* '"';