package com.sc2mod.andromeda.parser;

import com.sc2mod.andromeda.notifications.InternalProgramError;

public class SymbolStack {

	

	private Symbol[] stack = new Symbol[2048];
	private int stackPointer = 0;
	public Symbol elementAt(int i) {
		return stack[i];
	}

	public Symbol peek() {
		return stack[stackPointer-1];
	}
	
	/**
	 * Retrieves the ith topmost element from the stack (i == 0 is topmost element)
	 * @param i index from top
	 * @return the element
	 */
	public Symbol peek(int i){
		return stack[stackPointer-1-i];
	}

	public Symbol pop() {
		return stack[--stackPointer];
	}

	public Symbol push(Symbol item) {
		stack[stackPointer++] = item;
		return item;
	}

	public int size() {
		return stackPointer;
	}

	public void removeAllElements() {
		stackPointer = 0;
	}

	public boolean isEmpty() {
		return stackPointer == 0;
	}
}
