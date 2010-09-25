/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.forms;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.sc2mod.andromeda.program.Program;

public class MonitorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private MonitorPanel monitor;
	
	public MonitorFrame(){
	    //Caption
		this.setTitle(Program.name + " " + Program.VERSION);
		this.setIconImage((new ImageIcon("misc/icon.gif")).getImage());
		
		//Size und position
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int width = 800;
	    int height = 700;
	    this.setBounds(d.width/2-width/2, d.height/2-height/2, width, height);
		this.addWindowListener(new MyWindowListener());
		
		//Layout
		GridBagLayout gridbag = new GridBagLayout();
	    this.setLayout(gridbag);
	    		
	    GridBagConstraints c = new GridBagConstraints();
	    //setting a default constraint value
	    c.fill = GridBagConstraints.BOTH;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridheight = 1;
	    c.gridwidth = 1;	 
	    c.weightx = 1;
	    c.weighty = 1;
	    monitor = new MonitorPanel(true);
	  	this.add(monitor,c);
		 
	    
	    this.setJMenuBar(new MenuBar(true));
		
	}


	public MonitorPanel getMonitor() {
		return monitor;
	}
	


	public static void main(String[] args){
		new MonitorFrame().setVisible(true);
	}
}
