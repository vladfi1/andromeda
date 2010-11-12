/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.IOException;
import java.io.Reader;

public abstract class Source {

	/**
	 * Creates and returns a reader to read from this source
	 * @return a reader on this source
	 * @throws IOException if an IO exception occured
	 */
	public abstract Reader createReader() throws IOException;
	
	/**
	 * Returns the length of this source in bytes
	 * @return the length of this source in bytes
	 */
	public abstract long length();
	
	/**
	 * Returns true iff the resource actually exists
	 * @return existance of the resource
	 */
	public abstract boolean exists();
	
	
	/**
	 * Returns the name of this resource (not including the type)
	 * @return resource name
	 */
	public abstract String getName();
	
	/**
	 * Returns the full path to this resource
	 * @return resource path
	 */
	public abstract String getFullPath();
	
	/**
	 * Returns the description of this resource (containing the type of the resource)
	 * @return resource description
	 */
	public abstract String getTypeName();
	
	/**
	 * The anticipated language of this source.
	 * For files for example, this is determined by the file extension.
	 * @return
	 */
	public abstract Language getAnticipatedLanguage();
	
	/**
	 * Returns the path of this file in the source folder, converted to dot
	 * notation or null if this is no file source.
	 * 
	 * If the source folder is /a/b/c and the file's name is /a/b/c/d/e/F.a
	 * then this method would return d.e.F
	 * 
	 * 
	 */
	public abstract String getPathInSourceFolder();
	
}
