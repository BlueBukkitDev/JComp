package dev.blue.jcomp;

public class Token {
	
	private String raw;
	private TokenType type;
	
	/**
	 * Boolean variable is whether this token will be of an unknown 
	 * */
	public Token(String raw, TokenType type) throws InvalidTokenTypeException {
		this.type = type;
		this.raw = raw;
	}
	
	public String getRaw() {
		return raw;
	}
	
	public TokenType getType() {
		return type;
	}
}
