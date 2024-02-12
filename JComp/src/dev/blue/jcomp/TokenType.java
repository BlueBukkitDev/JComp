package dev.blue.jcomp;

import dev.blue.jcomp.lexing.LexerState;

public enum TokenType {
	OPEN, CLOSE, OPERATOR, KEYWORD, DEFINED, TYPE, COMMENT, BREAK, STRING, CONTENTS, SEQUENCER, COMPARATOR, PRIVACY, ACCESS, DEFINER, DATA, NONE;//FUNC, STRUCT, FIELD, TYPE, NUM, STRING, BINARY, 
	
	public static TokenType isDelimiter(char c, LexerState state) {
		switch(c) {
		//case '.' : if(isDefining != 3) {return TokenType.CONTENTS;}else return TokenType.NONE;//had to comment this out because of vague value and possible malstructuring. 
		case ',' : return TokenType.SEQUENCER;
		case '(' : return TokenType.OPEN;
		case ')' : return TokenType.CLOSE;
		case '[' : return TokenType.OPEN;
		case ']' : return TokenType.CLOSE;
		case '{' : return TokenType.OPEN;
		case '}' : return TokenType.CLOSE;
		default : return TokenType.NONE;
		}
	}
	
	public static TokenType isBreak(char c) {
		return (';' == c) ? TokenType.BREAK : TokenType.NONE;
	}
	
	public static TokenType isOperator(char c) {
		switch(c) {
		case '*' : return TokenType.OPERATOR;
		case '+' : return TokenType.OPERATOR;
		case '-' : return TokenType.OPERATOR;
		case '=' : return TokenType.OPERATOR;
		case '/' : return TokenType.OPERATOR;
		case '%' : return TokenType.OPERATOR;
		case '&' : return TokenType.COMPARATOR;
		case '|' : return TokenType.COMPARATOR;
		default: return TokenType.NONE;
		}
	}
	
	public static boolean isVisibilityToken(String s) {
		return (s.equals("there") || s.equals("here"));
	}
}
