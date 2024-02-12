package dev.blue.jcomp.lexing;

import dev.blue.jcomp.Option;
import dev.blue.jcomp.exceptions.UnexpectedLexerStateException;

public class LexerState {//It's really hard to set up a clean interaction between the lexer and the lexerstate. I want all comms to be via functions, no variables passed. 
	private int currentState;
	
	/**
	 *A LexerState is a tool for the Lexer to help follow the flow of a natural development pattern. When the 
	 *current state is that visibility has been defined, you then "expect" certain states to follow; in my 
	 *case, I "expect" either variability keyword or frame name. 
	 **/
	public LexerState() {
		currentState = STATE_NONE;
	}
	//These states help to give context to the lexer, allowing for one thing to have multiple meanings depending 
	//on how and where it is used. 
	private final int STATE_NONE = 0;
	private final int STATE_VISIBILITY = 1;
	private final int STATE_VARIABILITY = 2;
	private final int STATE_FIELD_NAME = 5;
	private final int STATE_FIELD_VALUE = 6;
	private final int STATE_FIELD_PARAMETERS = 7;
	private final int STATE_FRAME_NAME = 8;
	private final int STATE_FRAME_PARAMETERS = 9;
	
	private final int STATE_VALUE_OPERATOR = 10;
	
	private final int STATE_BREAK = 20;
	
	/**
	 * Determines whether the most recently read keyword was one used to determine the visibility of a field, 
	 * function, or frame (as of 2/11/24, these keywords are "here" and "there"). 
	 **/
	public boolean visibilityIsDefined() {
		return currentState == STATE_VISIBILITY;
	}
	
	/**
	 * Determines whether the most recently read keyword was one used to determine the variability or the 
	 * mutability of a field, function, or frame (as of 2/11/24, these keywords are "set" and "free"). 
	 **/
	public boolean variabilityIsDefined() {
		return currentState == STATE_VARIABILITY;
	}
	
	public boolean noneIsDefined() {
		return currentState == STATE_NONE;
	}
	
	public boolean fieldNameIsDefined() {
		return currentState == STATE_FIELD_NAME;
	}
	
	public boolean fieldValueIsDefined() {
		return currentState == STATE_FIELD_VALUE;
	}
	
	private void enterState(int state) throws UnexpectedLexerStateException {//make private, have public state changes that refer to this one. 
		if(!getExpectedState().contains(state)) {
			throw new UnexpectedLexerStateException("Attempted to enter state "+state+" but it was not a valid option (current state is "+currentState+").");
		}
		this.currentState = state;
	}
	
	public void enterVisibilityState() throws UnexpectedLexerStateException {
		enterState(STATE_VISIBILITY);
	}
	
	public void enterVariabilityState() throws UnexpectedLexerStateException {
		enterState(STATE_VARIABILITY);
	}
	
	public void enterFieldNameState() throws UnexpectedLexerStateException {
		enterState(STATE_FIELD_NAME);
	}
	
	public void enterFieldValueState() throws UnexpectedLexerStateException {
		enterState(STATE_FIELD_VALUE);
	}
	
	public void enterFrameNameState() throws UnexpectedLexerStateException {
		enterState(STATE_FRAME_NAME);
	}
	
	public Option<Integer> getExpectedState() throws UnexpectedLexerStateException {
		switch(currentState) {
		case STATE_NONE:return new Option<Integer>(STATE_VISIBILITY);//Also revaluing? If we are inside a scope... gonna need to manage scopes. Will be handled in LexerScope.
		case STATE_VISIBILITY:return new Option<Integer>(STATE_VARIABILITY);//
		case STATE_VARIABILITY:return new Option<Integer>(STATE_FIELD_NAME, STATE_FRAME_NAME);//
		
		case STATE_FIELD_NAME:return new Option<Integer>(STATE_FIELD_VALUE, STATE_FIELD_PARAMETERS);//We can declare any field with parameters, which when referenced must have inputs. 
		case STATE_FRAME_NAME:return new Option<Integer>(STATE_FRAME_PARAMETERS);
		
		case STATE_FIELD_VALUE:return new Option<Integer>(STATE_VALUE_OPERATOR, STATE_BREAK);
		case STATE_VALUE_OPERATOR:return new Option<Integer>(STATE_FIELD_VALUE);//These two can be chained over and over to assemble the value of a field.
		
		case STATE_BREAK:return new Option<Integer>(STATE_NONE);//If we are at a semicolon, we expect a none state so we can begin the next declarations. 
		default:throw new UnexpectedLexerStateException("LexerState function `getExpectedState` is incomplete. Current state "+currentState+" is not matched.");
		}
	}
}
