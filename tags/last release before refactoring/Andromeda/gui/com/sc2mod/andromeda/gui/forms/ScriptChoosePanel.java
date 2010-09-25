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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.sc2mod.andromeda.gui.forms.filetree.FileTree;
import com.sc2mod.andromeda.gui.forms.filetree.TreeListener;
import com.sc2mod.andromeda.program.Program;

public class ScriptChoosePanel extends JPanel{


	private static final long serialVersionUID = 1L;
	JSplitPane split1;
	FileTree tree;
	JPanel right;
	JTextPane text;
	JButton execute;
	JButton viewSource;
	JPanel compileOptionsPanel;
	private JComboBox runConfigBox;
	public JComboBox getRunConfigBox() {
		return runConfigBox;
	}
	public JCheckBox getRunMapCheckbox() {
		return runMapCheckbox;
	}
	private JCheckBox runMapCheckbox;
	private JTextField outNameField;
	public JTextField getOutNameField() {
		return outNameField;
	}
	public ScriptChoosePanel(){
	    
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
	    c.gridwidth = 10;	 
	    c.weightx = 0.2;
	    c.weighty = 0.2;
	    
	    this.add(split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tree = new FileTree(new File(".")),right = new JPanel()),c);
		split1.setDividerLocation(250);
		right.setLayout(new GridBagLayout());
		right.add(text = new JTextPane(),c);
		text.setEditable(false);
		text.setOpaque(false);
		Font f = new Font("Verdana",Font.PLAIN,12);
		
		JLabel label;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
	    c.gridy = GridBagConstraints.RELATIVE ;
	    c.gridheight = 4;
	    c.gridwidth = 10;	 
	    c.weightx = 0.0;
	    c.weighty = 0.0;
	    right.add(new JSeparator());
	    right.add(compileOptionsPanel = new JPanel(),c);
	    compileOptionsPanel.setBorder(BorderFactory.createTitledBorder("Compile Options"));
	    
	    compileOptionsPanel.setLayout(new GridBagLayout());
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridheight = 1;
	    c.gridwidth = 2;
	    compileOptionsPanel.add(runMapCheckbox = new JCheckBox("Run map after compilation"),c);
	    runMapCheckbox.setToolTipText("If checked, the map will be run after the compilation, if no compile error occurs");
	    c.gridwidth = 1;	    
	    c.gridy = 1;
	    compileOptionsPanel.add(label = new JLabel("Run configuration"),c);
	    c.gridx = 1;
	    compileOptionsPanel.add(runConfigBox = new JComboBox(new String[0]),c);
	    String tooltip = "Select the run configuration. The run configuration determines many parameters of the compilation. Run configurations can be altered / added in the configuration file.";
	    label.setToolTipText(tooltip);
	    runConfigBox.setToolTipText(tooltip);
	    
	    c.gridy = 2;
	    c.gridx = 0;
	    compileOptionsPanel.add(label = new JLabel("Output name"),c);
	    c.gridx = 1;
	    compileOptionsPanel.add(outNameField = new JTextField("{NAME}_COMP.SC2Map"),c);
	    tooltip = "Select the name for your map. {NAME} determines the input file name without the file extension. If you set this to {NAME}.SC2Map, the input map will be overwritten.";
	    label.setToolTipText(tooltip);
	    outNameField.setToolTipText(tooltip);
	    
	    c.gridy = 3;
	    c.gridx = 0;
	    c.gridwidth = 2;
	    compileOptionsPanel.add(execute = new JButton("Compile"),c);
	    execute.setEnabled(false);
	    
	    initRunConfigurations();
	    //right.add(execute = new JButton("Execute Script"),c);
	   // execute.setEnabled(false);
	    c.gridx = 3;
	   // right.add(viewSource = new JButton("View Source"),c);
	    
	    TreeListener listen = new TreeListener(tree.getTree(),text,execute,viewSource);
	   // viewSource.setEnabled(false);
	   // viewSource.addActionListener(listen);
	    execute.addActionListener(listen);    
		text.setFont(f);
		tree.addListener(listen);
	}
	private void initRunConfigurations() {
		String[] configs = Program.config.getPropertyCommaString("GENERAL", "runConfigs", "");
		String defaultConfig = Program.config.getPropertyString("GENERAL", "default", "debug");
		for(String s: configs)
			runConfigBox.addItem(s);
		runConfigBox.setSelectedItem(defaultConfig);
	}

}
