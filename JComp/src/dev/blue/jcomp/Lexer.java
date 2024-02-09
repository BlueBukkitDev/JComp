package dev.blue.jcomp;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
	private boolean isInBlockComment;
	private boolean isDebug;
	private boolean isInString;
	private String processed;
	private int isDefining;/*0 for normal stuff. 1 if visibility is found, 2 if this is 1 AND access is found. 
	If this is 1 and access/fn is not found, struct is assumed. If this is 2 and a keyword is found, error is 
	thrown. If this is 2 and a keyword is not found, then function or field name is assumed. Expect a colon 
	after the name, expect a brace after the struct. After a colon, expect a value; after the brace, expect 
	field=>type definitions. After definitions, expect fn definition or closing brace; after value, expect 
	semicolon. */
	
	public Lexer() {
		isInBlockComment = false;
		isDebug = true;
		isInString = false;
		processed = "";
	}
	
	public List<Token> lex(String line, int row) throws InvalidTokenTypeException {
		boolean terminatedLine = false;
		int index = 0;
		List<Token> tokens = new ArrayList<Token>();
		if(isDebug) System.out.print("["+row+"] ");
		TokenType type;
		String keyword;
		while (index < line.length()) {
			char current = line.charAt(index);
	        
			//COMMENTS
			////////////////////////////////////////////////////////////////
			
			int commStyle;
			if((commStyle = isComment(line, index)) > 0) {//We are a comment symbol
				if(commStyle == 1) {//A line comment
					if(!isInBlockComment) {//that is not nested inside a block comment
						if(isDebug) System.out.println(line.substring(index));
						terminatedLine = true;//we end the line here
						break;
					}//Or if it is, go to line 52? 'if is in block comment'
				}else if(commStyle == 2) {//We are a block comment symbol (...)
					if(isInBlockComment) {//If we are already in one
						isInBlockComment = false;//we get out
						if(isDebug) System.out.print("..."+processed+"...");//This part is verified to work 2/9/24
						processed = "";
						index += 3;//we iterate
						continue;//and we go on
					}else if(!isInBlockComment) {//Otherwise if we're not in one,
						isInBlockComment = true;//we get in
						//if(isDebug) System.out.print("...>");//This part is verified to work 2/9/24
						index += 3;//we iterate to the guts
						continue;//and we go on
					}
				}
			}
			if(isInBlockComment) {//If we are reading a comment already,
				processed += current;//we add the current char to the comment string,
				index++;//iterate forward,
				continue;//and move to the next char
			}
			
			//STRINGS
			////////////////////////////////////////////////////////////////
			
			if(isInString) {
				if(isString(current)) {
					isInString = false;
					tokens.add(new Token(processed, TokenType.STRING));
					if(isDebug) System.out.print("\""+processed+"\"");
					index++; 
					continue;
				}else {
					processed += current;
					index++;
					continue;
				}
			}else if(!isInString) {//Not in a string
				if(isString(current)) {//But we have a string char
					isInString = true;//then we have entered a string
					index++;//and should iterate to the next char
					continue;//skipping this one
				}
			}
			
			//COMPOUND SYMBOLS
			////////////////////////////////////////////////////////////////
			
			if(hasNext(line, index) && (type = isCompoundSymbol(current, line.charAt(index+1))) != TokenType.NONE) {//If has not next, cannot be compound. 
				char[] both = {current, line.charAt(index+1)};
				tokens.add(new Token(new String(both), type));
				if(isDebug) System.out.print(new String(both));
				index += 2;
				continue;
			}
			
			//SINGLE OPERATORS
			////////////////////////////////////////////////////////////////
			
			if((type = isOperator(current)) != TokenType.NONE) {
				tokens.add(new Token(Character.toString(current), type));
				if(isDebug) System.out.print(Character.toString(current));
				index++;
				continue;
			}
			
			//SCOPE SYMBOLS, DATA SEPARATORS
			////////////////////////////////////////////////////////////////
			
			if((type = isDelimiter(current)) != TokenType.NONE) {
				tokens.add(new Token(Character.toString(current), type));
				if(isDebug) System.out.print(current);
				index++;
				continue;
			}
			
			//SEMICOLON :D
			////////////////////////////////////////////////////////////////
			
			if((type = isBreak(current)) != TokenType.NONE) {
				tokens.add(new Token(Character.toString(current), type));
				if(isDebug) System.out.println(current);
				terminatedLine = true;
				index++;
				continue;
			}
			
			//KEYWORDS
			////////////////////////////////////////////////////////////////
			
			if((keyword = isKeyword(line, index)) != null) {
				System.out.print(keyword);
				index += keyword.length();
				continue;
			}

			index++;//if we reach this, we are probably gliding along past some whitespace. 
		}
		if (!terminatedLine) {System.out.println("");}
		return tokens;
	}
	//--End of Lex Function--//
	
	public void cleanup() {
		isInBlockComment = false;
	}
	
	private boolean hasNext(String line, int index) {
		return line.length() > index+1;
	}
	
	/**
	 * returns whether the line has `count` characters remaining to be read (exclusive). <br>
	 * So if you want to know whether your indexed character (p) is followed by 3 more (riv), 
	 * then final parameter should be 3, not 4 (length of total sought word `priv`). 
	 * */
	private boolean hasNextOf(String line, int index, int count) {
		return line.length() > index+count;
	}
	
	private String isKeyword(String line, int index) {
		if(hasNextOf(line, index, 2) && line.substring(index, index+3).equals("pub")) {
			return "pub";
		}
		if(hasNextOf(line, index, 3) && line.substring(index, index+4).equals("priv")) {
			return "priv";
		}
		if(hasNextOf(line, index, 2) && line.substring(index, index+3).equals("set")) {
			return "set";
		}
		if(hasNextOf(line, index, 3) && line.substring(index, index+4).equals("free")) {
			return "free";
		}
		return null;
	}
	
	private TokenType isDelimiter(char c) {
		switch(c) {
		case '.' : return TokenType.CONTENTS;
		case ',' : return TokenType.SEQUENCER;
		case '(' : return TokenType.OPEN;
		case ')' : return TokenType.CLOSE;
		case '[' : return TokenType.OPEN;
		case ']' : return TokenType.CLOSE;
		case '{' : return TokenType.OPEN;
		case '}' : return TokenType.CLOSE;
		case ':' : return TokenType.OPERATOR;
		default : return TokenType.NONE;
		}
	}
	
	private TokenType isBreak(char c) {
		return (';' == c) ? TokenType.BREAK : TokenType.NONE;
	}
	
	private TokenType isOperator(char c) {
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
	
	private boolean isString(char c) {
		return ('"' == c) ? true : false;
	}
	
	private int isComment(String line, int index) {
		if(hasNext(line, index)) {//single-line comment
			if(line.charAt(index) == '/' && line.charAt(index+1) == '/') {
				return 1;
			}
			if(hasNextOf(line, index, 2)) {//multi-line comment
				if(line.substring(index, index+3).equals("...")) {
					return 2;
				}
			}
		}
		
		return 0;
	}
	
	private TokenType isCompoundSymbol(char here, char next) {
		switch(here) {
		case '=':if(next == '>') {return TokenType.OPERATOR;} else if (next == '=') {return TokenType.COMPARATOR;}
		case '!':if(next == '=') return TokenType.COMPARATOR;
		
		case '+':if(next == '=') return TokenType.OPERATOR;
		case '-':if(next == '=') return TokenType.OPERATOR;
		case '*':if(next == '=') return TokenType.OPERATOR;
		case '/':if(next == '=') return TokenType.OPERATOR;
		case '%':if(next == '=') return TokenType.OPERATOR;
		default: return TokenType.NONE;
		}
	}
}
/*pub, priv, set, free, fn, true, false, for, if, ok, nok, string, num, binary, *, +, -, =, /, %, <, >, ?, :, =>, !, while,*/