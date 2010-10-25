package com.sc2mod.andromeda.program;

import java.util.HashSet;

public class ToDo {

	private static HashSet<String> messages = new HashSet<String>();
	public static void println(String message){
		if(messages.contains(message)) return;
		messages.add(message);
		System.err.println("TODO: "  + message);
	}
}
