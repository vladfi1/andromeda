package com.sc2mod.andromeda.parser.cup;


/**
 * Creates the Symbols interface, which CUP uses as default
 *
 * @version last updated 27-03-2006
 * @author Michael Petter
 */

/* *************************************************
  Interface SymbolFactory
  
  interface for creating new symbols  
  You can also use this interface for your own callback hooks
  Declare Your own factory methods for creation of Objects in Your scanner!
 ***************************************************/
public class SymbolFactory {
    // Factory methods
	private Symbol[] symStack = new Symbol[1024];
	private int stackPointer = 0;
	
	private Symbol getSymbol(){
		Symbol symbol;
		if(stackPointer == 0) symbol = new Symbol();
		else{
			symbol = symStack[--stackPointer];
			symbol.used_by_parser = false;
		}
		
		return symbol;
	}
	
	public Symbol newSymbol(String name, int sym){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.value = null;
		return symbol;
	}
	
	public Symbol newSymbol(int sym, int l, int r, Object val){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.left = l;
		symbol.right = r;
		symbol.value = val;
		return symbol;
	}
	
	public Symbol newSymbol( int id, Symbol l,
			Symbol r, Object value) {
		Symbol symbol = getSymbol();
		symbol.sym = id;
		symbol.left = l.left;
		symbol.right = r.right;
		symbol.value = value;
		return symbol;
	}
	
	public Symbol newSymbol(int sym, Object val){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.left = -1;
		symbol.right = -1;
		symbol.value = val;
		return symbol;
	}
	
	public Symbol newSymbol(int sym, int l, int r){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.left = l;
		symbol.right = r;
		symbol.value = null;
		return symbol;
	}
	
	public Symbol newSymbol(int sym, Symbol l, Symbol r){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.left = l.left;
		symbol.right = r.right;
		symbol.value = null;
		return symbol;
	}
	
	Symbol newSymbol(int sym, int state){
		Symbol symbol = getSymbol();
		symbol.sym = sym;
		symbol.parse_state = state;
		return symbol;
	}
	
	

	public void releaseSymbol(Symbol sym){
		if(stackPointer >= symStack.length){
			//stack too small, throw away token (i.e. just do nothing)
			return;
			//stack too small, double it
//			Symbol[] newStack = new Symbol[symStack.length*2];
//			System.arraycopy(symStack, 0, newStack, 0, symStack.length);
//			symStack = newStack;
		}
		symStack[stackPointer++] = sym;
	}
    /**
     * Construction of start symbol
     */
    public Symbol startSymbol(String name, int id, int state){
    	return newSymbol(id,state);
    }


}
