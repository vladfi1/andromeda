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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.sc2mod.andromeda.program.Program;


public class FileTree extends JPanel implements ActionListener
{ 
	
	private JTree myTree;
	private File myDir;
	private JButton refreshButton = new JButton("Refresh");
	private static final long serialVersionUID = 1L;
	
	private static class FileTreeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;
		static Icon gslIcon = new ImageIcon(Program.getSystemResource("icons/AndromedaSmall.png"));
		static Icon w3xIcon = new ImageIcon(Program.getSystemResource("icons/sc2.png"));
		public FileTreeRenderer() {
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			FileTreeNode m = (FileTreeNode)value;
			if(m.isFolder){
				leaf = false;
				if(sel)expanded = true;
			}
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			if(leaf) {
				if(m.getUserObject().toString().endsWith(".a"))
					setIcon(gslIcon);
				else
					setIcon(w3xIcon);
			}

			return this;
		}


	}
	
	static class FileTreeNode extends DefaultMutableTreeNode{
		
		
		private static final long serialVersionUID = 1L;
	
		boolean isFolder;
		public FileTreeNode(Object input, boolean isFolder){
			super(input);
			this.isFolder = isFolder;			
		}
		
	}
	  
	
	
	public void refresh(File dir){
		myTree.setModel(new DefaultTreeModel(addNodes(null,dir)));
        TreeModel m = myTree.getModel();
        FileTreeNode root = (FileTreeNode)m.getRoot();
        FileTreeNode[] path = new FileTreeNode[2];
        path[0]	= root;
        for(int i=0,max = root.getChildCount();i<max;i++){
        	FileTreeNode cur = (FileTreeNode) root.getChildAt(i);
        	if(cur.getUserObject().equals("script")){
        		path[1] = cur;
        	}
        }
        myTree.expandPath(new TreePath(path));
        
	}
    /** Construct a FileTree */
    public FileTree(File dir) {
    	myDir = dir;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Files"));
        
        // Make a tree list with all the nodes, and make it a JTree
        final JTree tree = new JTree(addNodes(null, dir));
        
        //Use my renderer
        tree.setCellRenderer(new FileTreeRenderer());
        
        //Only one file selectable
        TreeSelectionModel myModel = new DefaultTreeSelectionModel();
        myModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);        
        tree.setSelectionModel(myModel);
        
        //expand the script folder
//        TreeModel m = tree.getModel();
//        MyTreeNode root = (MyTreeNode)m.getRoot();
//        MyTreeNode[] path = new MyTreeNode[2];
//        path[0]	= root;
//        for(int i=0,max = root.getChildCount();i<max;i++){
//        	MyTreeNode cur = (MyTreeNode) root.getChildAt(i);
//        	if(cur.getUserObject().equals("script")){
//        		path[1] = cur;
//        	}
//        }
//        tree.expandPath(new TreePath(path));
        
        // Lastly, put the JTree into a JScrollPane.
        JScrollPane myPane = new JScrollPane();
        myPane.getViewport().add(tree);
        add(BorderLayout.CENTER, myPane);
        myTree = tree;
  
        //Add a refresh button
        add(BorderLayout.SOUTH,refreshButton);
        refreshButton.addActionListener(this);
    }
    
    public void addListener(TreeListener l){
    	myTree.addTreeSelectionListener(l);
    	myTree.addMouseListener(l);
    }

    /** Add nodes from under "dir" into curTop. Highly recursive. */
    FileTreeNode addNodes(FileTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        FileTreeNode curDir = new FileTreeNode(dir.getName(),true);
        if (curTop != null) {   // should only be null at root
            curTop.add(curDir);
        }
        Vector<String> ol = new Vector<String>();
        String[] tmp = dir.list();
        for (int i=0; i<tmp.length; i++)
            ol.addElement(tmp[i]);
        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        File f;
        Vector<String> files = new Vector<String>();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i=0; i<ol.size(); i++) {
            String thisObject = (String)ol.elementAt(i);
            String newPath;
            if (curPath.equals("."))
                newPath = thisObject;
            else
                newPath = curPath + File.separator + thisObject;
            if ((f = new File(newPath)).isDirectory())
                addNodes(curDir,  f);
            else{
            	String name = f.getName().toLowerCase();
            	//Show only gsl and w3x files
            	if(name.toLowerCase().endsWith(".sc2map")||name.endsWith(".a"))
            		files.addElement(thisObject);
            }
        }
        // Pass two: for files.
        for (int fnum=0; fnum<files.size(); fnum++)
            curDir.add(new FileTreeNode(files.elementAt(fnum),false));
        return curDir;
    }

	public JTree getTree() {
		return myTree;
	}
	public void actionPerformed(ActionEvent arg0) {
		refresh(myDir);
		
	}
	public JButton getRefreshButton() {
		return refreshButton;
	}

    /*public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }*/

}

