package dev.blue.jcomp;

import dev.blue.jcomp.exceptions.InvalidTokenTypeException;

/**
 *A token is a unit of text associated with a particular context, denoted by the TokenType. Also included 
 *in this class is a stack of final Strings that denote the text sought out in the lexed code; changing 
 *these would change the keywords being checked for by the Lexer. 
 **/
public class Token {
	
	public static final String VISIBLE_EXTERNAL = "there";
	public static final String VISIBLE_INTERNAL = "there";
	public static final String VAR_MUTABLE = "free";
	public static final String VAR_CONSTANT = "set";
	public static final String COMMENT_LINE = "//";
	public static final String COMMENT_BLOCK = "...";
	public static final String DEFINER = ":";
	
	private String raw;
	private TokenType type;
	
	/**
	 * 
	 **/
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
