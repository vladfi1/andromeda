package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VisitorAdapter;

/**
 * Convenient visitor that just passes the result down the tree as
 * long as no specific visit method for a node is overridden
 * @author gex
 *
 * @param <P> the input for each visit method
 * @param <R> the result of each visit method
 */
public class TreeScanVisitor<P,R> extends VisitorAdapter<P, R>{
	
	public R visitDefault(SyntaxNode s, P state) {
		return s.childrenAccept(this,state);
	}
}
