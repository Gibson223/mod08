lexer grammar Exercise11;

fragment LETTER : ('a'..'z'|'A'..'Z');
fragment LETTER_DIGIT : ('a'..'z'|'A'..'Z'|'0'..'9');
WORD : LETTER LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT;

