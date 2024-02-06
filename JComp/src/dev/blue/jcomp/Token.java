package dev.blue.jcomp;

public class Token {
	
	private String raw;
	private TokenType type;
	
	public Token(String raw) throws InvalidTokenTypeException {
		if(raw.startsWith("//")||raw.startsWith("...")) {
			type = TokenType.COMMENT;
			this.raw = raw;
			return;
		}
		switch(raw) {
		case "+":type = TokenType.OPERATOR;break;
		case "-":type = TokenType.OPERATOR;break;
		case "*":type = TokenType.OPERATOR;break;
		case "/":type = TokenType.OPERATOR;break;
		case "%":type = TokenType.OPERATOR;break;
		case "=":type = TokenType.OPERATOR;break;
		case "=>":type = TokenType.OPERATOR;break;
		case "==":type = TokenType.OPERATOR;break;
		case "+=":type = TokenType.OPERATOR;break;
		case "-=":type = TokenType.OPERATOR;break;
		case "*=":type = TokenType.OPERATOR;break;
		case "/=":type = TokenType.OPERATOR;break;
		case "%=":type = TokenType.OPERATOR;break;
		case "!=":type = TokenType.OPERATOR;break;
		default: throw new InvalidTokenTypeException();
		}
		this.raw = raw;
	}
}
