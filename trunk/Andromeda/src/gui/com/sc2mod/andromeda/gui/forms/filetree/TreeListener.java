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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.sc2mod.andromeda.gui.forms.ScriptChoosePanel;
import com.sc2mod.andromeda.gui.forms.ScriptInfoParser;
import com.sc2mod.andromeda.gui.forms.ScriptInfoParser.InfoParseResult;
import com.sc2mod.andromeda.gui.forms.filetree.FileTree.FileTreeNode;
import com.sc2mod.andromeda.gui.jobs.JobExecMap;
import com.sc2mod.andromeda.program.Program;

public class TreeListener extends MouseAdapter implements TreeSelectionListener, ActionListener {
    
	private JTree tree;
	private JTextPane output;
	private ScriptInfoParser parser = new ScriptInfoParser();
	private JButton exec;
	private JButton view;
	private File currentSelectedFile;

	public TreeListener(JTree t,JTextPane out, JButton exec, JButton view
			){
		this.tree = t;
		this.exec = exec;
		this.view = view;
		this.output = out;
//		Document notSelectDoc = new DefaultStyledDocument();
//    	try {
//			notSelectDoc.insertString(0, "Script info:\n\n", ScriptInfoParser.capt);
//			notSelectDoc.insertString(notSelectDoc.getLength(), "- select a script file -", null);
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}
//		Document mapSelectDoc = new DefaultStyledDocument();
//    	try {
//			mapSelectDoc.insertString(0, "This is a wc3 map:\n\n", ScriptInfoParser.capt);
//			mapSelectDoc.insertString(mapSelectDoc.getLength(), "If you execute this file, the program will search for a trigger called \"script.ini\" (which has to be a comment) and will execute its content as GSL code. If the map contains no trigger with that name or that trigger is not a comment you will receive an error.", null);
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}
//		nothingSelected = new InfoParseResult(notSelectDoc,false,false,null);
//		mapSelected = new InfoParseResult(mapSelectDoc,true,false,null);

	}
	
	private File assemblePath(TreePath p){
        Object[] path = p.getPath() ;
        StringBuilder b = new StringBuilder();
        for(int i=1,max = path.length;i<max;i++){
        	b.append(path[i].toString());
        	if(i<max-1) b.append("/");
        }
        return new File(b.toString());
	}


	private void updateStatus(File file){
		currentSelectedFile = file;
		if(file != null){
			exec.setEnabled(true);
		} else {
			exec.setEnabled(false);
		}
	}
	
	/*private String getPath(TreePath e){
		String path = "D:\\GOSI";
		boolean skip = true;
		for(Object o:e.getPath()){
			if(skip){
				skip = false;
				continue;
			}
			MyTreeNode n = (MyTreeNode)o;
			path += "\\" + n.getUserObject().toString();
		}
		return path;
		
	}*/
	public void valueChanged(TreeSelectionEvent e) {
        String file = ((FileTreeNode)e.getPath().getLastPathComponent()).getUserObject().toString();
        if(file.toLowerCase().endsWith(".sc2map")){
        	updateStatus(assemblePath(e.getPath()));
        }
        else {
        	updateStatus(null);
        }
    }
    
    public void mousePressed(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1) {
            if(e.getClickCount() == 1) {
            }
            else if(e.getClickCount() == 2) {
                Object[] path = selPath.getPath() ;
                StringBuilder p = new StringBuilder();
                for(int i=1,max = path.length;i<max;i++){
                	p.append(path[i].toString());
                	if(i<max-1) p.append("/");
                }
                String pathStr = p.toString();
                if(pathStr.toLowerCase().endsWith("sc2map")){
                	//This is a map and can be executed
                	execMap(new File(pathStr));
                }
            }
        }
    }
    private void execMap(File f) {
    	Program.guiController.getMainFrame().setSelectedTab(1);
		Program.guiController.getMainFrame().getMonitor().setSelectedTab(0);
    	ScriptChoosePanel sp = Program.guiController.getMainFrame().getScriptChoosePanel();
    	String outName = sp.getOutNameField().getText();
    	String name = f.getName().split("\\.")[0];
    	outName = outName.replaceAll("\\{NAME\\}", name); 
    	File out = new File(f.getAbsoluteFile().getParentFile(),outName);
    	System.out.println(out.getName());
    	String runConf = sp.getRunConfigBox().getSelectedItem().toString();
    	boolean runMap = sp.getRunMapCheckbox().isSelected();
		Program.jobHandler.setJob(new JobExecMap(f,out,
    							runConf
    							,runMap));
    }
	
    private void execCurrentFile(){
//		Program.getMainFrame().setSelectedTab(1);
//		Program.getMainFrame().getMonitor().setSelectedTab(0);
//		Program.setJob(new JobExecFile(currentFile.file,false));    	
    }
	public void actionPerformed(ActionEvent e) {
		execMap(currentSelectedFile);
//		if(e.getSource()==view){
//			String editor = "\""+ Program.getIni().getPropertyString("editor", "editorFile", "")+ "\"";
//			if(editor.equals("")){
//				JOptionPane.showInternalMessageDialog(null, "You haven't specified an editor for viewing script files in the .ini! ", "No editor selected", JOptionPane.ERROR_MESSAGE);
//				return;
//			}
//			try {
//				Runtime.getRuntime().exec(editor + " \"" + this.currentFile.file.getAbsolutePath() + "\"", null);
//			} catch (IOException e1) {
//				JOptionPane.showInternalMessageDialog(null, "An error occured, while trying to start the specified editor. Check if you have specified the right editorFile in your .ini! ", "Editor not found", JOptionPane.ERROR_MESSAGE);
//			}
//		} else if(e.getSource()==exec){
//			if(currentFile==mapSelected){
//        		execCurrentMap();
//        	} else 	execCurrentFile();
//       
//		} else {
//			parser.clearCache();
//		}
		
	}
}
