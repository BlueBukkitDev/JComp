package dev.blue.jcomp;

import dev.blue.jcomp.exceptions.InvalidTokenTypeException;

public class Token {
	
	public static final String VIS_EXT = "there";
	public static final String VIS_IN = "there";
	public static final String VAR_MUT = "free";
	public static final String VAR_CONST = "set";
	
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
