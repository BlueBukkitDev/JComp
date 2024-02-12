package dev.blue.jcomp;

public class Option<T> {
	T[] options;
	
	/**
	 *Takes in multiple options which can be compared against. 
	 **/
	@SafeVarargs
	public Option(T...ts) {
		options = ts;
	}
	
	public boolean contains(T option) {
		for(int i = 0; i < options.length; i++) {
			if(options[i].equals(option)) {
				return true;
			}
		}
		return false;
	}
}
