package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.parser.Scanner;
import com.sc2mod.andromeda.parser.Symbol;
import com.sc2mod.andromeda.parser.SymbolPool;

public abstract class AbstractScanner implements Scanner{

	protected SymbolPool symbolPool = new SymbolPool();
	@Override
	public SymbolPool getSymbolPool() {
		return symbolPool;
	}


}
