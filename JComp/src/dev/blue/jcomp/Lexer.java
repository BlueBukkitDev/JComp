package dev.blue.jcomp;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
	private boolean isInBlockComment;
	private boolean isDebug;
	
	public Lexer() {
		isInBlockComment = false;
		isDebug = false;
	}
	
	public List<Token> lex(String line, int row) throws InvalidTokenTypeException {
		int index = 0;
		List<Token> tokens = new ArrayList<Token>();

		while (index < line.length()) {
			char current = line.charAt(index);
	        
			//COMMENTS
			////////////////////////////////////////////////////////////////
			
			int commStyle;
			if((commStyle = isComment(line, index)) > 0) {//Check for comment comes before compound, since if we find a comment, we read nothing inside
				if(commStyle == 1 && !isInBlockComment) {
					if(isDebug) System.out.println("["+row+", "+index+"]>> Found a line comment");
					break;
				}else if(commStyle == 2) {
					if(isDebug) System.out.println("["+row+", "+index+"]>> "+(isInBlockComment ? "Exiting a block comment" : "Entering a block comment"));
					isInBlockComment = !isInBlockComment;
					index += 3;
					continue;
				}
			}
			if(isInBlockComment) {
				index++;
				continue;
			}
			
			//COMPOUND SYMBOLS
			////////////////////////////////////////////////////////////////
			
			if(hasNext(line, index) && isCompoundSymbol(current, line.charAt(index+1))) {//If has not next, cannot be compound. 
				char[] both = {current, line.charAt(index+1)};
				tokens.add(new Token(new String(both)));
				if(isDebug) System.out.println("["+row+", "+index+"]>> Found a compound: "+new String(both));
				index += 2;
				continue;
			}
			
			//SINGLE OPERATORS
			////////////////////////////////////////////////////////////////
			
			if(isOperator(current)) {
				tokens.add(new Token(Character.toString(current)));
				if(isDebug) System.out.println("["+row+", "+index+"]>> Found a single operator");
				index++;
				continue;
			}

			index++;
		}
		return tokens;
	}
	
	public void cleanup() {
		isInBlockComment = false;
	}
	
	private boolean hasNext(String line, int index) {
		return line.length() > index+1;
	}
	
	private boolean isOperator(char c) {
		switch(c) {
		case '*' : return true;
		case '+' : return true;
		case '-' : return true;
		case '=' : return true;
		case '/' : return true;
		case '%' : return true;
		default: return false;
		}
	}
	
	private int isComment(String line, int index) {
		if(hasNext(line, index)) {//single-line comment
			if(line.charAt(index) == '/' && line.charAt(index+1) == '/') {
				return 1;
			}
		}else return 0;
		if(hasNext(line, index+1)) {//multi-line comment
			if(line.charAt(index) == '.' && line.charAt(index+1) == '.' && line.charAt(index+2) == '.') {
				return 2;
			}
		}
		return 0;
	}
	
	private boolean isCompoundSymbol(char here, char next) {
		switch(here) {
		case '=':if(next == '>' || next == '=') return true;
		case '!':if(next == '=') return true;
		
		case '+':if(next == '=') return true;
		case '-':if(next == '=') return true;
		case '*':if(next == '=') return true;
		case '/':if(next == '=') return true;
		case '%':if(next == '=') return true;
		default: return false;
		}
	}
}
/*pub, priv, set, free, fn, true, false, for, if, ok, nok, string, num, binary, *, +, -, =, /, %, <, >, ?, :, =>, !, while,*/