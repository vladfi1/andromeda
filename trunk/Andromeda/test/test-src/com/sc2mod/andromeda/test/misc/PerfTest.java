package com.sc2mod.andromeda.test.misc;

import java.util.HashMap;

import com.sc2mod.andromeda.util.StopWatch;

public class PerfTest {

	public static HashMap<C, Integer> m = new HashMap<C, Integer>();
	private static class C{
		int x;

		public int hashCode() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
	}
	
	
	public static void main(String[] args) {
		C c = new C();
		HashMap<C, Integer> map = PerfTest.m;
		map.put(c,5);
		for(int i=0;i<1000000000;i++){
			int x = c.hashCode();
			
		}
		StopWatch w = new StopWatch();
		w.start();
		for(long i=0;i<1000000000l;i++){
			int x = c.hashCode();
		
		}
		w.printTime("");
	}
}
