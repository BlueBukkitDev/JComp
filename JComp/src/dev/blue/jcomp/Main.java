package dev.blue.jcomp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.blue.jcomp.exceptions.InvalidTokenTypeException;
import dev.blue.jcomp.exceptions.UnexpectedLexerStateException;
import dev.blue.jcomp.lexing.Lexer;

public class Main {

	//args... "java -jar jcomp.jar file.smth file2.smth etc.smth" would compile all them together. Instead, could read a local file called jcomp.jcml to find everything to be compiled.
	public static void main(String[] args) {
		Lexer lexer = new Lexer();
		int index = 1;
		for(String each:readLines(new File("min.smth"))) {//set up as min to run a minimally executable example. 
			try {
				lexer.lex(each, index);
			} catch (InvalidTokenTypeException | UnexpectedLexerStateException e) {
				e.printStackTrace();
			}
			index++;
		}
		lexer.cleanup();//Call cleanup after every file that is read, before beginning to lex the next file. 
	}
	
	
	private static List<String> readLines(File file) {
		BufferedReader reader = null;
		List<String> lines = new ArrayList<String>();
		String currentLine = null;

        try {
        	reader = new BufferedReader(new FileReader(file));
			while ((currentLine = reader.readLine()) != null) {
			    lines.add(currentLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return lines;
	}
}
