package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.NoResultVisitorAdapter;

public class NoResultTreeScanVisitor<P> extends NoResultVisitorAdapter<P> {
	
	
	public void visitDefault(SyntaxNode s, P state) {
		s.childrenAccept(this,state);
	}
}
