package com.sc2mod.andromeda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class WindowsProcessKiller {

	public boolean kill(String name, boolean killAll){
		Process p;
		boolean killed = false;
		try {
			p = Runtime.getRuntime().exec("tasklist");
			BufferedReader sr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			//Read the first 3 lines (they re just header)
			sr.readLine();
			sr.readLine();
			sr.readLine();
			while((line = sr.readLine())!= null){
				//Find the desired process
				if(line.startsWith(name)){
					killed = killProcess(line);
					if(!killAll){
						break;
					}
						
				}
			}
			
			sr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NumberFormatException e){
			e.printStackTrace();
			return false;
		}
		
		return killed;

		
	}
	
	private boolean killProcess(int pid) throws IOException{
		Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
		return true;
	}
	
	private static Pattern LINE_PATTERN = Pattern.compile("");
	private boolean killProcess(String line) throws IOException {
		
		Integer procId = Integer.parseInt(line.substring(26,26+8).trim());
		return killProcess(procId);
	}

}
