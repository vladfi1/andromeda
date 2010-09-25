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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.sc2mod.andromeda.program.Program;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabs;
	private MonitorPanel monitor;

	private ScriptChoosePanel scriptChoose;
	
	public MainFrame(){
	    //Caption
		this.setTitle(Program.name + " " + Program.VERSION);

		this.setIconImage((new ImageIcon(Program.getSystemResource("icons/Andromeda.png"))).getImage());
		
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
	    c.insets = new Insets(4, 4, 4, 4);
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridheight = 10;
	    c.gridwidth = 3;	 
	    c.weightx = 1;
	    c.weighty = 1;
	    
	    tabs = new JTabbedPane();
	    this.add(tabs,c);
	   	scriptChoose = new ScriptChoosePanel();
	   	tabs.addTab("Script Browser",scriptChoose);
	   	monitor = new MonitorPanel(false);
	   	tabs.addTab("Execution Monitor", monitor);

	    
	    //this.setJMenuBar(new MenuBar(false));
		
	}

	public void setSelectedTab(int index){
		tabs.setSelectedIndex(index);
	}

	public MonitorPanel getMonitor() {
		return monitor;
	}

	public ScriptChoosePanel getScriptChoosePanel(){
		return scriptChoose;
	}
}
