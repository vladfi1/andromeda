/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.jobs;

import com.sc2mod.andromeda.program.Log;
import com.sc2mod.andromeda.program.Program;

public class WorkerThread extends Thread{

	private boolean busy;
	
	public WorkerThread(Job j){
		busy = true;
		currentJob = j;
	}
	
	public WorkerThread(){
		busy = false;
	}
	
	private Job currentJob;
	
	public void run() {
		
		try {
			currentJob.execute();
		} catch (OutOfMemoryError e) {
			Program.log.println("--- Out of memory, restart the program! If this error occurs often you should increase max heap space of your Java Virtual Machine ---");
		} finally {
			System.gc();
			Program.jobHandler.fireJobFinished(currentJob);
			busy = false;				
		}
	}

	public boolean isBusy(){
		return busy;
	}
	
	
	
}
