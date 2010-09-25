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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.sc2mod.andromeda.gui.misc.EchoTarget;
import com.sc2mod.andromeda.gui.misc.GuiLog;
import com.sc2mod.andromeda.gui.misc.LogWriter;



public class AutoscrollPane extends JTextPane implements LogWriter, EchoTarget {

	private CaretThread scrollThread;
	private Object monitor = new Object();

	private class CaretThread extends Thread{
		private Document d;
		private JTextPane j;
		
		public CaretThread(Document d,JTextPane j){
			this.d = d;
			this.j = j;
			this.setDaemon(true);
		}
		private boolean changes = false;
		public void changed(){
			changes = true;
		}
		public void run(){
			while(true){
				synchronized(monitor){
					if(changes){
						
						j.setCaretPosition(d.getLength());
							
					}
						
					changes = false;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private static final long serialVersionUID = 1L;
	public AutoscrollPane(){
	    this.setEditable(false);
	    this.setBackground(Color.BLACK);
	    this.setForeground(Color.WHITE);
	    scrollThread = new CaretThread(this.getDocument(),this);
	    scrollThread.start();
	    Font font = new Font("Courier New",Font.PLAIN,12);
	    this.setFont(font);
	}
	public void append(String s, AttributeSet se){
		Document d = this.getDocument();
		synchronized (monitor) {
			try {
				int i = d.getLength();
				if(i>25000)
					try {
						d.remove(0, i-25000);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				d.insertString(d.getLength(), s, se);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}		
		scrollThread.changed();
		//new CaretThread(d,this).start();
		//this.setCaretPosition(d.getLength());
	}
	
	public void print(String s, EchoType t) {
		if(t==EchoType.WARNING){
			append(s,GuiLog.STYLE_WARNING);
		} else 
			append(s,null);		
	}
}
