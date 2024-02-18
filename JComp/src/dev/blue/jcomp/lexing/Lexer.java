package dev.blue.jcomp.lexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.blue.jcomp.Token;
import dev.blue.jcomp.TokenType;
import dev.blue.jcomp.exceptions.InvalidTokenTypeException;
import dev.blue.jcomp.exceptions.UnexpectedLexerStateException;

public class Lexer {
	private boolean isInBlockComment;
	private boolean isDebug;
	private boolean isInString;
	private String processed;
	private List<Token> tokens = new ArrayList<Token>();
	private LexerState state;
	private int lineIndex;
	private int index;
	private String line;
	
	public Lexer() {
		isInBlockComment = false;
		isDebug = true;
		isInString = false;
		processed = "";
		state = new LexerState();
		lineIndex = 0;
	}
	
	public void lex(String line, int row) throws InvalidTokenTypeException, UnexpectedLexerStateException {
		this.line = line;
		lineIndex++;
		boolean terminatedLine = false;
		index = 0;
		if(isDebug) System.out.print("["+row+"] ");
		TokenType type;
		String keyword = "";
		while (index < line.length()) {
			
			int result = canLexComments(terminatedLine);
			if(result == LexerResult.COMMENT_LEX_LINE) {
				return;
			}else if(result == LexerResult.COMMENT_LEX_BLOCK) {
				continue;
			}
			
			if(canLexCompoundOperator()) {
				continue;
			}
			
			if(canLexCompoundComparator()) {
				continue;
			}
			
			if(canLexOperator()) {//Gonna need to check for left- and right-hand values. 
				continue;
			}
			
			if(canLexComparator()) {
				continue;
			}
			
			if(canLexSequencer()) {
				continue;
			}
			
			if(canLexHeir()) {
				continue;
			}
			
			if(canLexKeywords(keyword)) {
				continue;
			}
			
			if(canLexDefinitions()) {
				continue;
			}
			
			if(state.fieldNameIsDefined()) {
				if(isNumeric(line.charAt(index))) {//we are defining a number
					processed += line.charAt(index);
					index++;
					continue;
				}else if(canLexStrings(line.charAt(index))) {//we are defining a string
					continue;
				}
			}
			
			if(canLexBreak()) {
				continue;
			}

			index++;
		}
		if (!terminatedLine) {System.out.println("");}//shouldn't the ! not be there?
	}
	//--End of Lex Function--//
	
	/**
	 *Tokenizes the value of a field currently being processed. To do so, it adds the token to the list as TokenType.DATA, then 
	 *resets the processed string. 
	 **/
	private void tokenizeCurrentValue() throws InvalidTokenTypeException {
		tokens.add(new Token(processed, TokenType.DATA));
		System.out.print(processed.trim());
		processed = "";
	}
	
	/**
	 *Checks whether a field name is being defined or has been defined; if it has been defined (we have encountered a Definer token), 
	 *then it resets the processed string and enters the fieldNameState.
	 **/
	private boolean canLexDefinitions() throws InvalidTokenTypeException, UnexpectedLexerStateException {
		if(!state.variabilityIsDefined()) {
			return false;
		}
		if(isDefiner()) {
			processed = processed.trim();
			validateFieldName(processed);
			tokens.add(new Token(processed, TokenType.DEFINED));
			tokens.add(new Token(":", TokenType.DEFINER));
			if(isDebug) System.out.print(processed+":");
			index++;
			processed = "";
			state.enterFieldNameState();
			return true;
		}else {
			processed += line.charAt(index);
			index++;
			return true;
		}
	}
	
	/**
	 *Validates the supplied field name by trimming whitespace and then ensuring it is at least 1 char long. 
	 *@param name - the name of the field
	 *@throws InvalidTokenTypeException if field name is less than 1 char long
	 **/
	private void validateFieldName(String name) throws InvalidTokenTypeException {
		if(name.trim().length() == 0) {
			throw new InvalidTokenTypeException("Field name declared on line "+lineIndex+", column "+index+" is invalid. Check length and characters. ");
		}
	}
	
	/**
	 *
	 **/
	private boolean canLexKeywords(String keyword) throws InvalidTokenTypeException, UnexpectedLexerStateException {
		keyword = keyword.trim();
		if((keyword = isKeyword()) == null) {
			return false;
		}
		if(isDebug) System.out.print(keyword);
		if(TokenType.isVisibilityToken(keyword)) {
			lexVisibility(keyword);
		}else if(TokenType.isVariabilityToken(keyword)) {
			lexVariability(keyword);
		}
		index += keyword.length();
		return true;
	}
	
	/**
	 *
	 **/
	private void lexVisibility(String keyword) throws InvalidTokenTypeException, UnexpectedLexerStateException {
		if(state.noneIsDefined()) {
			tokens.add(new Token(keyword, TokenType.PRIVACY));
			state.enterVisibilityState();
		}else {
			//throw lexer error; invalid privacy placement
		}
	}
	
	/**
	 *
	 **/
	private void lexVariability(String keyword) throws InvalidTokenTypeException, UnexpectedLexerStateException {
		if(state.visibilityIsDefined()) {
			tokens.add(new Token(keyword, TokenType.ACCESS));
			state.enterVariabilityState();
		}else {
			//throw lexer error; invalid access placement, no privacy
		}
	}
	
	/**
	 *
	 **/
	private int canLexComments(boolean terminatedLine) {
		int commStyle;
		if((commStyle = isComment()) > 0) {//We are a comment symbol
			if(commStyle == 1) {//A line comment
				if(!isInBlockComment) {//that is not nested inside a block comment
					if(isDebug) System.out.println(line.substring(index));
					terminatedLine = true;//we end the line here
					return LexerResult.COMMENT_LEX_LINE;
				}//Or if it is, go to line 52? 'if is in block comment'
			}else if(commStyle == 2) {//We are a block comment symbol (...)
				if(isInBlockComment) {//If we are already in one
					isInBlockComment = false;//we get out
					if(isDebug) System.out.print("...>"+processed+"<...");//Here we print the begin and end tokens, sandwiching the processed comment.
					processed = "";
					index += 3;//we iterate out
					return LexerResult.COMMENT_LEX_BLOCK;//and we go on
				}else if(!isInBlockComment) {//Otherwise if we're not in one,
					isInBlockComment = true;//we get in
					//if(isDebug) System.out.print("...>");//This part is verified to work 2/9/24
					index += 3;//we iterate to the guts
					return LexerResult.COMMENT_LEX_BLOCK;//and we go on
				}
			}
		}
		if(isInBlockComment) {//If we are reading a comment already,
			processed += line.charAt(index);//we add the current char to the comment string,
			index++;//iterate forward,
			return LexerResult.COMMENT_LEX_BLOCK;//and move to the next char
		}
		return LexerResult.COMMENT_LEX_FAIL;
	}
	
	private boolean canLexStrings(char current) throws InvalidTokenTypeException {
		if(isInString) {
			if(isString(current)) {
				isInString = false;
				tokens.add(new Token(processed, TokenType.STRING));
				if(isDebug) System.out.print("\""+processed+"\"");
				processed = "";
				index++; 
				return true;
			}else {
				processed += current;
				index++;
				return true;
			}
		}else if(!isInString) {//Not in a string
			if(isString(current)) {//But we have a string char
				isInString = true;//then we have entered a string
				index++;//and should iterate to the next char
				return true;//skipping this one
			}
		}
		return false;
	}
	
	public void cleanup() {
		isInBlockComment = false;
		isInString = false;
		processed = "";
		state = new LexerState();
		lineIndex = 0;
	}
	
	/**
	 * returns whether the line has `count` characters remaining to be read (exclusive). <br>
	 * So if you want to know whether your indexed character (p) is followed by 3 more (riv), 
	 * then final parameter should be 3, not 4 (length of total sought word `priv`). 
	 * */
	private boolean hasNextOf(String line, int index, int count) {
		return line.length() > index+count;
	}
	
	/**
	 *Checks whether the given char is a Definer token. If so, returns TokenType.DEFINER. 
	 *Otherwise, returns TokenType.NONE. 
	 **/
	private boolean isDefiner() {
		if(hasNextOf(line, index, Token.DEFINER.length()) && line.substring(index, index+Token.DEFINER.length()).equals(Token.DEFINER)) {
			return true;
		}
		return false;
	}
	
	/**
	 *Checks whether the given char is numeric. This includes [0123456789.-] and excludes all else. 
	 **/
	private boolean isNumeric(char c) {
		switch(c) {
		case '0':return true;
		case '1':return true;
		case '2':return true;
		case '3':return true;
		case '4':return true;
		case '5':return true;
		case '6':return true;
		case '7':return true;
		case '8':return true;
		case '9':return true;
		case '.':return true;
		case '-':return true;
		default:return false;
		}
	}
	
	/**
	 *Checks whether the given line has a keyword beginning at the given index. If so, returns the 
	 *keyword found there. If no keyword is found, returns null. 
	 **/
	private String isKeyword() {
		if(hasNextOf(line, index, Token.VISIBLE_EXTERNAL.length()-1) && line.substring(index, index+Token.VISIBLE_EXTERNAL.length()).equals(Token.VISIBLE_EXTERNAL)) {
			return Token.VISIBLE_EXTERNAL;
		}
		if(hasNextOf(line, index, Token.VISIBLE_INTERNAL.length()-1) && line.substring(index, index+Token.VISIBLE_INTERNAL.length()).equals(Token.VISIBLE_INTERNAL)) {
			return Token.VISIBLE_INTERNAL;
		}
		if(hasNextOf(line, index, Token.FIELD_CONSTANT.length()-1) && line.substring(index, index+Token.FIELD_CONSTANT.length()).equals(Token.FIELD_CONSTANT)) {
			return Token.FIELD_CONSTANT;
		}
		if(hasNextOf(line, index, Token.FIELD_MUTABLE.length()-1) && line.substring(index, index+Token.FIELD_MUTABLE.length()).equals(Token.FIELD_MUTABLE)) {
			return Token.FIELD_MUTABLE;
		}
		return null;
	}
	
	/**
	 *Checks whether the checked char is one used to denote the wrapping of a String. 
	 **/
	private boolean isString(char c) {
		return '"' == c;
	}
	
	/**
	 *Checks whether the provided index of the provided string is the beginning of a comment declaration. 
	 **/
	private int isComment() {
		if(hasNextOf(line, index, Token.COMMENT_LINE.length()-1) && TokenType.isLineCommentToken(line.substring(index, index+Token.COMMENT_LINE.length()))) {
			return 1;
		}
		if(hasNextOf(line, index, Token.COMMENT_BLOCK.length()-1) && TokenType.isBlockCommentToken(line.substring(index, index+Token.COMMENT_BLOCK.length()))) {
			return 2;
		}
		return 0;
	}
	
	/**
	 *Checks whether the passed chars form a valid compound symbol, e.g. operators and comparators. 
	 *If so, creates the token and adds it. 
	 * @throws InvalidTokenTypeException 
	 **/
	private boolean canLexCompoundOperator() throws InvalidTokenTypeException {
		String compound;
		if((compound = getCompoundOperator()) == null) {
			return false;
		}
		if(Arrays.asList(Token.COMPOUND_OPS).contains(compound)) {
			tokens.add(new Token(compound, TokenType.OPERATOR));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexCompoundComparator() throws InvalidTokenTypeException {
		String compound;
		if((compound = getCompoundComparator()) == null) {
			return false;
		}
		if(Arrays.asList(Token.COMPOUND_COMPS).contains(compound)) {
			tokens.add(new Token(compound, TokenType.COMPARATOR));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexOperator() throws InvalidTokenTypeException {
		String compound;
		if((compound = getOperator()) == null) {
			return false;
		}
		if(Arrays.asList(Token.OPERATORS).contains(compound)) {
			tokens.add(new Token(compound, TokenType.OPERATOR));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexComparator() throws InvalidTokenTypeException {
		String compound;
		if((compound = getComparator()) == null) {
			return false;
		}
		if(Arrays.asList(Token.COMPARATORS).contains(compound)) {
			tokens.add(new Token(compound, TokenType.COMPARATOR));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexSequencer() throws InvalidTokenTypeException {
		String compound;
		if(hasNextOf(line, index, Token.SEQUENCER.length()-1) && (compound = line.substring(index, index+Token.SEQUENCER.length())).equals(Token.SEQUENCER)) {
			tokens.add(new Token(compound, TokenType.SEQUENCER));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexHeir() throws InvalidTokenTypeException {
		String compound;
		if(hasNextOf(line, index, Token.HEIR.length()-1) && (compound = line.substring(index, index+Token.HEIR.length())).equals(Token.HEIR)) {
			tokens.add(new Token(compound, TokenType.HEIR));
			index += compound.length();
			return true;
		}
		return false;
	}
	
	private boolean canLexBreak() throws InvalidTokenTypeException {
		String compound;
		if(hasNextOf(line, index, Token.BREAK.length()-1) && (compound = line.substring(index, index+Token.BREAK.length())).equals(Token.BREAK)) {
//			if(state.fieldValueIsDefined()) {
//				if(processed.length() > 0) {
//					tokenizeCurrentValue();
//					state.enterFieldValueState();
//				}
//			}
			if(!state.fieldValueIsDefined()) {
				throw new InvalidTokenTypeException("Misplaced line break `"+Token.BREAK+"` at line "+lineIndex+", column "+index);
			}
			tokens.add(new Token(compound, TokenType.BREAK));
			if(isDebug) System.out.println(line.charAt(index));
			index++;
			return true;
		}
		return false;
	}
	
	private String getOperator() {
		for(int i = 0; i < Token.OPERATORS.length; i++) {
			if(hasNextOf(line, index, Token.OPERATORS[i].length()-1) && line.substring(index, index+Token.OPERATORS[i].length()).equals(Token.OPERATORS[i])) {
				return Token.OPERATORS[i];
			}
		}
		return null;
	}
	
	private String getComparator() {
		for(int i = 0; i < Token.COMPARATORS.length; i++) {
			if(hasNextOf(line, index, Token.COMPARATORS[i].length()-1) && line.substring(index, index+Token.COMPARATORS[i].length()).equals(Token.COMPARATORS[i])) {
				return Token.COMPARATORS[i];
			}
		}
		return null;
	}
	
	private String getCompoundOperator() {
		for(int i = 0; i < Token.COMPOUND_OPS.length; i++) {
			if(hasNextOf(line, index, Token.COMPOUND_OPS[i].length()-1) && line.substring(index, index+Token.COMPOUND_OPS[i].length()).equals(Token.COMPOUND_OPS[i])) {
				return Token.COMPOUND_OPS[i];
			}
		}
		return null;
	}
	
	private String getCompoundComparator() {
		for(int i = 0; i < Token.COMPOUND_COMPS.length; i++) {
			if(hasNextOf(line, index, Token.COMPOUND_COMPS[i].length()-1) && line.substring(index, index+Token.COMPOUND_COMPS[i].length()).equals(Token.COMPOUND_COMPS[i])) {
				return Token.COMPOUND_COMPS[i];
			}
		}
		return null;
	}
}
/*there, here, set, free, fn, true, false, for, if, ok, nok, string, num, binary, *, +, -, =, /, %, <, >, ?, :, =>, !, while,*/