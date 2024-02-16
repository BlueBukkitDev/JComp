package dev.blue.jcomp;

public enum TokenType {
	OPEN, CLOSE, OPERATOR, KEYWORD, DEFINED, TYPE, COMMENT, BREAK, STRING, HEIR, SEQUENCER, COMPARATOR, PRIVACY, ACCESS, DEFINER, DATA, NONE;//FUNC, STRUCT, FIELD, TYPE, NUM, STRING, BINARY, 
	
	public static TokenType isBreak(char c) {
		return (';' == c) ? TokenType.BREAK : TokenType.NONE;
	}
	
	public static boolean isVisibilityToken(String s) {
		return (s.equals(Token.VISIBLE_EXTERNAL) || s.equals(Token.VISIBLE_INTERNAL));
	}
	
	public static boolean isVariabilityToken(String s) {
		return (s.equals(Token.FIELD_CONSTANT) || s.equals(Token.FIELD_MUTABLE));
	}
	
	public static boolean isLineCommentToken(String s) {
		return s.equals(Token.COMMENT_LINE);
	}
	
	public static boolean isBlockCommentToken(String s) {
		return s.equals(Token.COMMENT_BLOCK);
	}
}
