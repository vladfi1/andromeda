/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.forms.filetree;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilters {

	public static class OnlyFoldersFilter extends FileFilter{

		public boolean accept(File arg0) {
			if(arg0.isDirectory())return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "directories";
		}
		
	}
}
