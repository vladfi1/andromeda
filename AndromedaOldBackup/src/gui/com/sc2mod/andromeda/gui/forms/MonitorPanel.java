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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.sc2mod.andromeda.gui.jobs.Job;
import com.sc2mod.andromeda.gui.jobs.JobHandler;
import com.sc2mod.andromeda.gui.jobs.JobListener;
import com.sc2mod.andromeda.gui.misc.UIOutputStream;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.util.logging.Logger;

public class MonitorPanel extends JPanel implements ActionListener,JobListener{
	

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabs;
	private JButton restart = new JButton ("Restart last script");
	private JButton abort = new JButton("Abort script");
	private JButton exit = new JButton("Okay, Exit");
	private AutoscrollPane echoPane;
	public AutoscrollPane getEchoPane() {
		return echoPane;
	}

	private AutoscrollPane statusPane;
	private JButton clearEcho = new JButton("Clear");
	private JButton clearStatus = new JButton("Clear");
	
	public MonitorPanel(boolean monitorOnly){
	    
		//Layout
		GridBagLayout gridbag = new GridBagLayout();
	    this.setLayout(gridbag);
	    		
	    GridBagConstraints c = new GridBagConstraints();
	    //setting a default constraint value
	    c.fill = GridBagConstraints.BOTH;
	    c.insets = monitorOnly?new Insets(2,2,2,2):new Insets(20, 8, 4,8);
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridheight = 10;
	    c.gridwidth = 10;	 
	    c.weightx = 0.2;
	    c.weighty = 0.2;
	    
	    JPanel echo = new JPanel();
	    JPanel status = new JPanel();
	    JPanel info = new JPanel();
	    info.add(new JLabel("not used yet"));
	    this.add(tabs = new JTabbedPane(),c);
	    tabs.addTab("Script output", echo);
	    tabs.addTab("Log & Warnings", status);
	    //tabs.addTab("Script status", info);
	    echo.setLayout(new GridBagLayout());
	    status.setLayout(new GridBagLayout());
	    
	    echo.add(new JScrollPane(echoPane = new AutoscrollPane()),c);
	    status.add(new JScrollPane(statusPane = new AutoscrollPane()),c);
	    
	    //System.err umleiten
	    //System.setErr(new PrintStream(new UIOutputStream(statusPane,Log.error)));
	    
	    c.gridx = 10;
	    c.weightx = 0;
	    c.weighty = 0;
	    c.gridheight = 1;
	    c.gridwidth = 1;
		c.gridy = 0;
		
		status.add(clearStatus,c);
		echo.add(clearEcho,c);
		clearStatus.addActionListener(this);
		clearEcho.addActionListener(this);
		
		//Log.setLogWriter(statusPane);
		//Log.setErrorWriter(echoPane);
		//Program.getScript().setEchoTarget(echoPane);
	
		c.gridx = 0;
		c.gridy = 10;
		c.insets = monitorOnly?new Insets(2,4,4,4):new Insets(4, 8, 8,8);
	    this.add(restart,c);
		restart.addActionListener(this);
		restart.setEnabled(false);
	
		
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		this.add(abort,c);
		abort.setEnabled(false);
		abort.addActionListener(this);
		
		if(monitorOnly){
			c.gridx = 9;
			this.add(exit,c);
			exit.addActionListener(this);
		}
		
		
		
		Program.jobHandler.addJobListener(this);
	
	}
	
	
	public void setSelectedTab(int index){
		tabs.setSelectedIndex(index);
	}

	public void actionPerformed(ActionEvent e) {
		JobHandler jh = Program.jobHandler;
		if(e.getSource()==clearStatus){
			statusPane.setText("");
		} else if(e.getSource()==clearEcho){
			echoPane.setText("");
		} else if(e.getSource()==restart){
			jh.setJob(jh.getLastJob());
		} else if(e.getSource()==abort){
			throw new InternalProgramError("Aborting not possible!");
			//Program.getScript().abort();
		} else if(e.getSource()==exit){
			System.exit(0);
		}
		
	}

	public void jobStarted(Job j) {
		restart.setEnabled(false);
		exit.setEnabled(false);
		abort.setEnabled(true);
	}

	public void jobFinished(Job j) {
		if(Program.jobHandler.getLastJob()!=null)restart.setEnabled(true);		
		exit.setEnabled(true);
		abort.setEnabled(false);
	}
	

}
