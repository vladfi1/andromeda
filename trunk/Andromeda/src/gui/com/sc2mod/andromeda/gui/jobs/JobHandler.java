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

import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.sc2mod.andromeda.program.Program;


public class JobHandler {
	private Object monitor = new Object();
	private Job lastJob = null;
	private LinkedList<JobListener> jobStartListeners = new LinkedList<JobListener>();
	private static WorkerThread workerThread = new WorkerThread();
	
	public void fireJobFinished(Job j){
		for(JobListener l: jobStartListeners){
			l.jobFinished(j);
		}
		
	}
	public void addJobListener(JobListener j){
		jobStartListeners.add(j);
	}
	public boolean setJob(Job j){
		synchronized(monitor){
			if (workerThread.isBusy()){
				Program.guiController.showMessage("Still busy!", "The program is still busy, you cannot start another task yet", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(j.isRepeatable())
				lastJob = j;
			workerThread = new WorkerThread(j);		

		}
		for(JobListener l: jobStartListeners){
			l.jobStarted(j);
		}
		workerThread.start();
		return true;
	}

	public Job getLastJob() {
		return lastJob;
	}
	
}
