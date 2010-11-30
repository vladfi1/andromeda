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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import com.sc2mod.andromeda.gui.jobs.Job;
import com.sc2mod.andromeda.gui.jobs.JobListener;
import com.sc2mod.andromeda.program.Program;

public class MenuBar extends JMenuBar implements ActionListener, JobListener {

	private static final long serialVersionUID = 1L;

	private JMenu MFile = new JMenu("File");
	private JMenuItem MISync = new JMenuItem("Synchronize Data with WC3");
	
	private JMenuItem MIExit = new JMenuItem("Exit");
	
	private JMenu MHelp = new JMenu("Help");
	private JMenuItem MIDocumentation = new JMenuItem("Documentation");
	private JMenuItem MIAbout = new JMenuItem("About " + Program.name);
	
	private JMenu MScript = new JMenu("Script");
	private JMenuItem MIExecute = new JMenuItem("Execute Script...");
	private JMenuItem MIRestart = new JMenuItem("Restart last script");
	
	private JMenu MSettings = new JMenu("Settings");
	private JMenuItem MIFolders = new JMenuItem("Folders...");
	

	
	public MenuBar(boolean monitorOnly){
		this.add(MFile);
		MFile.add(MISync);
		MISync.addActionListener(this);
		MFile.add(new JSeparator());
		MFile.add(MIExit);
		MIExit.addActionListener(this);
		
		if(!monitorOnly){
		this.add(MScript);
			MScript.add(MIExecute);
			MIExecute.addActionListener(this);
			Program.jobHandler.addJobListener(this);
			MScript.add(MIRestart);
			MIRestart.addActionListener(this);
			MIRestart.setEnabled(false);
		}
		
		this.add(MSettings);
		MSettings.add(MIFolders);
		MIFolders.addActionListener(this);

		this.add(MHelp);
		MHelp.add(MIDocumentation);
		MIDocumentation.addActionListener(this);
		MHelp.add(MIAbout);
		MIAbout.addActionListener(this);

	}

	public void actionPerformed(ActionEvent act) {
		if(act.getSource() == MIExit){
			System.exit(0);
		}
		else if(act.getSource() == MIAbout){
			Program.guiController.showMessage("Manual", Program.name + " " + Program.VERSION + "\nby Jan \"gex\" Finis, 2010\nmail: gekko_tgh@gmx.de\n\n" +
					"Visit http://www.sc2mod.com for more information.", JOptionPane.INFORMATION_MESSAGE);
	
		}
		else if(act.getSource() == MIDocumentation){
			try {
				Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe","/c", "\"" + new File("").getAbsolutePath() + "\\doc\\index.html\""});

				int o;
				while((o =p.getInputStream().read()) != -1){
					System.out.print((char)o);
				}
			} catch (IOException e) {
				Program.guiController.showMessage("Unable to start browser", "Unable to start your browser, starting JAVA browser.\nHowever this looks ugly :(\n Maybe rather open doc/index.html with your favorite browser!", JOptionPane.WARNING_MESSAGE);
				JEditorPane pane = new JEditorPane();
				JScrollPane scroll = new JScrollPane(pane);
				pane.setEditable(false);
				try {
					pane.setPage( "File:///" + new File("").getAbsolutePath() + "\\doc\\index.html");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MyListenerFrame f = new MyListenerFrame("Documentation",pane);
				f.add(scroll);
				f.setSize(800,600);
				f.setVisible(true);
			}
		} else if(act.getSource() == MISync){
			//Program.getMainFrame().setSelectedTab(1);
			//Program.getMainFrame().getMonitor().setSelectedTab(1);
			//Program.setJob(new UpdateFiles());
		} else if(act.getSource() == MIRestart){
		
			
			//Program.getMainFrame().setSelectedTab(1);
			//Program.getMainFrame().getMonitor().setSelectedTab(0);
			//Program.setJob(Program.getLastJob());
		} else if(act.getSource() == MIExecute){
//			CustomFilter filter = new CustomFilter(".w3x,.gsl");
//			JFileChooser openChoose = new JFileChooser(".");
//			openChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			openChoose.setMultiSelectionEnabled(false);
//			openChoose.setDialogTitle("Choose a file to execute");
//			openChoose.setFileFilter(filter);
//		
//			if(openChoose.showDialog(null,"Okay") == JFileChooser.APPROVE_OPTION){
//				Program.getMainFrame().setSelectedTab(1);
//				Program.getMainFrame().getMonitor().setSelectedTab(0);
//				Program.setJob(new JobExecFile(openChoose.getSelectedFile()));
//			}
			
		} else if(act.getSource() == MIFolders){
//			Settings.getFolders().setVisible(true);
			
		}

		
	}
	
	private class MyListenerFrame extends JFrame implements HyperlinkListener {

		private JEditorPane htmlPane;
		public MyListenerFrame(String s,JEditorPane htmlPane){
			super(s);
			this.htmlPane = htmlPane;
			htmlPane.addHyperlinkListener(this);
			this.setIconImage((new ImageIcon("misc/icon.gif")).getImage());
			
		}
		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					htmlPane.setPage(event.getURL());
				} catch (IOException ioe) {
					// Some warning to user
				}
			}
		}

	}
	
	private class CustomFilter extends FileFilter {
		private String ext;
		private String[] exts;
		public CustomFilter(String ext){
			this.ext = ext;
			if(ext == null){
				exts = null;
				ext = "All";
				return;
			}
			
			exts = ext.split(",");
		}
		
	    public boolean accept(File f) {
	      // Auch Unterverzeichnisse anzeigen
	      if (f.isDirectory())
	        return true;
	    
	      if(exts == null) return true;
	      String fileName = f.getName().toLowerCase();
	      for(String ex : exts){
	    	  if (fileName.endsWith(ex)) return true;
	      }
	      return false;
	      }

	    public String getDescription() { return ext + " files"; }  

	}

	public void jobFinished(Job j) {
		MIExecute.setEnabled(true);
		if(Program.jobHandler.getLastJob()!=null)MIRestart.setEnabled(true);
		MISync.setEnabled(true);
	}

	public void jobStarted(Job j) {
		MIExecute.setEnabled(false);
		MIRestart.setEnabled(false);
		MISync.setEnabled(false);
	}
}
