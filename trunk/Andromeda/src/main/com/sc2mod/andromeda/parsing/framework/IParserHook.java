package com.sc2mod.andromeda.parsing.framework;

import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;

public interface IParserHook {

	void importRead(ImportNode i);
	
	void packageDeclRead(PackageDeclNode p);
	
	void includeRead(IncludeNode in);

	public static IParserHook DO_NOTHING = new IParserHook() {
		@Override
		public void packageDeclRead(PackageDeclNode p) {}
		
		@Override
		public void importRead(ImportNode i) {}

		@Override
		public void includeRead(IncludeNode in) {}
	};
}
