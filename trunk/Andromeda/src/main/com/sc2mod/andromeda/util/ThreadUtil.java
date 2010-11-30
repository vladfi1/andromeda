package com.sc2mod.andromeda.util;

public class ThreadUtil {

	public static void wait(Object monitor){
		try {
			monitor.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void sleepMillisec(int lengthInMillisec){
		try {
			ThreadUtil.class.wait(lengthInMillisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
