package com.sc2mod.andromeda.util;

public class ThreadUtil {

	public static void wait(Object monitor){
		try {
			monitor.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
