package dev.blue.jcomp;

import dev.blue.jcomp.exceptions.InvalidTokenTypeException;

/**
 *A token is a unit of text associated with a particular context, denoted by the TokenType. Also included 
 *in this class is a stack of final Strings that denote the text sought out in the lexed code; changing 
 *these would change the keywords being checked for by the Lexer. 
 **/
public class Token {
	
	public static final String VISIBLE_EXTERNAL = "there";
	public static final String VISIBLE_INTERNAL = "here";
	public static final String FIELD_MUTABLE = "free";
	public static final String FIELD_CONSTANT = "set";
	public static final String COMMENT_LINE = "//";
	public static final String COMMENT_BLOCK = "...";
	public static final String DEFINER = ":";
	public static final String BREAK = ";";
	public static final String[] COMPOUND_OPS = {"=>", "+=", "-=", "*=", "/=", "%=", "++", "--", "**", "//"};//considering using ** for square and // for sqrt
	public static final String[] COMPOUND_COMPS = {"<=", ">=", "==", "!=", "=/="};//last option is not used, but could be used as a != operator.
	public static final String[] OPERATORS = {"=", "+", "-", "*", "/", "%", "^", "@"};//Considering using @ for getting the pointer of a variable.
	public static final String[] COMPARATORS = {"<", ">", "?", "|", "&"};
	public static final String[] SCOPES_OPEN = {"(", "[", "{"};
	public static final String[] SCOPES_CLOSE = {")", "]", "}"};
	public static final String SEQUENCER = ",";
	public static final String HEIR = ".";
	public static final String STRING_WRAPPER = "\"";
	public static final String CHAR_WRAPPER = "'";
	
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
