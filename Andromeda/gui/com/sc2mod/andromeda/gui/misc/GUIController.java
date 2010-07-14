/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.misc;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.sc2mod.andromeda.gui.forms.MainFrame;
import com.sc2mod.andromeda.gui.jobs.JobHandler;
import com.sc2mod.andromeda.program.Program;

public class GUIController {
	
	private MainFrame mainFrame;
	
	public GUIController(){
		
		//look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			System.out.println("Platform GUI not supported, using Swing standard Look and Feel");
		}

		
		mainFrame = new MainFrame();
		
		
		//We use gui logging
		Program.log = new GuiLog(mainFrame.getMonitor().getEchoPane());
		
		mainFrame.setVisible(true);
		
	}

	
	public void showMessage(String caption, String message, int messageType){
		
		JOptionPane.showMessageDialog(mainFrame, "The program is still busy, you cannot start another task yet", "Still busy!", JOptionPane.ERROR_MESSAGE);
	}


	public MainFrame getMainFrame() {
		return mainFrame;
	}
}
