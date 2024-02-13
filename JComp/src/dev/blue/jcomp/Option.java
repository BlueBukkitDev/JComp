package dev.blue.jcomp;

import java.util.Arrays;

/**
*This class was made with the express purpose of providing integer or string options, so the 
*use of anything else as a type may and may not function as intended. <br>
*Suggested use is to create a few final values elsewhere, put them into an Option at a 
*particular compiler/lexer state, and then utilize those options to validate following 
*states. 
**/
public class Option<T> {
	private T[] options;
	
	/**
	 *Takes in multiple options which can be compared against for validation later.
	 *
	 *@param ts - an array of options to be considered valid
	 **/
	@SafeVarargs
	public Option(T...ts) {
		options = ts;
	}
	
	/**
	 *Checks the options provided at instantiation to see whether this option is valid. 
	 *@param option - the option being validated
	 *@return boolean - whether the option is valid
	 **/
	public boolean contains(T option) {
		return Arrays.asList(options).contains(option);
	}
}
