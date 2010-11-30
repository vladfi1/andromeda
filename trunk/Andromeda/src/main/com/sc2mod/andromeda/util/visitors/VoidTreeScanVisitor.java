package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

/**
 * Same as TreeScanVisitor without result and parameter
 * @author gex
 *
 * @param <P> the input for each visit method
 * @param <R> the result of each visit method
 */
public class VoidTreeScanVisitor extends VoidVisitorAdapter{
	
	public void visitDefault(SyntaxNode s) {
		s.childrenAccept(this);
	}
}
