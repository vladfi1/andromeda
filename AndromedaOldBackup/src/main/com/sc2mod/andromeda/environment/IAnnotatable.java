/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;

public interface IAnnotatable {
	
	
	boolean hasAnnotation(String name);
		
	void setAnnotationTable(HashMap<String, AnnotationNode> annotations);

	HashSet<String> getAllowedAnnotations();

	/**
	 * Returns a description for this annotatable (for error messages)
	 * @return description
	 */
	String getDescription();
	
	/**
	 * Callback method that is called after annotations have been processed.
	 * The method is not called if the annotatable had no annotations
	 */
	void afterAnnotationsProcessed();

}
