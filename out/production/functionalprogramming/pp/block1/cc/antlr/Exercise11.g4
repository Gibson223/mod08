lexer grammar Exercise11;

LETTER : ('a'..'z'|'A'..'Z');
LETTER_DIGIT : ('a'..'z'|'A'..'Z'|'0'..'9');
WORD : LETTER LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT LETTER_DIGIT;

