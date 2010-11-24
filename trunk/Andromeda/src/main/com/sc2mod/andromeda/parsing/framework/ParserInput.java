package com.sc2mod.andromeda.parsing.framework;

import java.io.Reader;

public class ParserInput {
	
	private final Reader reader;
	private final int inputNumber;
	
	public ParserInput(Reader reader, int inputNumber) {
		super();
		this.reader = reader;
		this.inputNumber = inputNumber;
	}

	public Reader getReader(){
		return reader;
	}
	
	public int getInputNumber(){
		return inputNumber;
	}
	
}
