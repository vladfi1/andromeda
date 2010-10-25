package com.sc2mod.andromeda.semAnalysis;

/**
 * The expression context can be used by visitors to identify
 * in which context an expression is used.
 * @author gex
 *
 */
public enum ExpressionContext {
	/**
	 * The expression is used in context of a statement or something comparable (like a in a; ).
	 */
	STATEMENT,
	
	/**
	 * The expression is used in the context of another misc expression without special demands (like a in (Int)a )
	 */
	DEFAULT,
	
	/**
	 * The expression is used as an lValue (left side of an assignment, like a in a = 5 )
	 */
	LVALUE,
	
	/**
	 * The expression is used as a l- and rValue (left side of a += expression or operand of a ++ operation like a in a++ )
	 */
	LRVALUE,
	
	/**
	 * The expression is used as the left side of a scope prefix operation (like a in a.b )
	 */
	SCOPE_PREFIX;
}
