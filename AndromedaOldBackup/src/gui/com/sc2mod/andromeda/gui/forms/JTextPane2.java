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

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class JTextPane2 extends JTextPane
{
	private static final long serialVersionUID = 1L;
	boolean wrap=true ;

   public JTextPane2()
   {
      // impliziter Aufruf von super()
   }

   public JTextPane2(boolean wrap)
   {
      // impliziter Aufruf von super()
      this.wrap = wrap;
   }

   public JTextPane2(StyledDocument doc)
   {
      super(doc);
   }

   public boolean getScrollableTracksViewportWidth()
   {
      if (wrap)
         return super.getScrollableTracksViewportWidth() ;
      else
         return false ;
   }

   public void setSize(Dimension d)
   {
      if(!wrap)
      {
         if (d.width < getParent().getSize().width)
            d.width = getParent().getSize().width;
      }
      super.setSize(d);
   }

   //Sets the line-wrapping policy of the JTextPane2
   //By default this property is true, d.h. Zeilenumbruch
   void setLineWrap(boolean wrap)
   {
      setVisible(false);  // alten Zustand verschwinden lassen (notwendig)
      this.wrap = wrap ;
      setVisible(true);   // neuen Zustand anzuzeigen (notwendig)
   }
}

