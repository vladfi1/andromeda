/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.problems;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public interface SourceLocation {
	
	

	/**
	 * Gets the byte offset of the message's position in the source file
	 * @return the offset in the file
	 */
	public int getFileOffset();
	
	/**
	 * Gets the length of the marked position
	 * @return length
	 */
	public int getLength();
	
	/**
	 * Gets the line number in the source file
	 * @return line number
	 */
	public int getLine();
	
	/**
	 * Gets the starting column of the marked position in the source file
	 * @return starting column
	 */
	public int getColumn();
	
	/**
	 * Returns the source of the message
	 */
	public Source getSource();
	
	/**
	 * Returns a descripting string containing the line number, the line content and an underlining of the location.
	 * @return
	 */
	public String getDescriptionString();
}
