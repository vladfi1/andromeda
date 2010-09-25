package com.sc2mod.andromeda.util;

public class StopWatch {

	private long lastTime;
	private boolean started = false;
	
	/**
	 * Starts the stop watch.
	 */
	public void start(){
		lastTime = System.currentTimeMillis();
		started = true;
	}
	
	/**
	 * Gets the time in milliseconds since the watch was started.
	 * Throws an InternalProgramError if the stopwatch hasn't started before.
	 * @return
	 */
	public long getTime(){
		return System.currentTimeMillis() - lastTime;
	}
	
	public void printTime(String prefix){
		System.out.println(prefix + getTime() + "ms");
	}
}
