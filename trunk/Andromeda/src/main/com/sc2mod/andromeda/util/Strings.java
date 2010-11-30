package com.sc2mod.andromeda.util;

import java.util.Collection;

public final class Strings {

	private Strings(){}
	
	public static String mkString(Object[] objs, String seperator){
		StringBuilder sb = new StringBuilder(objs.length*10);
		for(int i=0,length=objs.length;i<length;){
			sb.append(String.valueOf(objs[i]));
			i++;
			if(i < length){
				sb.append(seperator);
			}
		}
		return sb.toString();
	}
	
	public static String mkString(Collection<?> objs, String seperator){
		if(objs.isEmpty()) return "";
		StringBuilder sb = new StringBuilder(objs.size()*10);
		for(Object o : objs){
			sb.append(String.valueOf(o));
			sb.append(seperator);
			
		}
		return sb.substring(0,sb.length()-seperator.length());
	}
	
	private static String[] spaces;
	
	static{
		spaces = new String[40];
		spaces[0] = "";
		for(int i=1;i<spaces.length;i++){
			spaces[i] = " " + spaces[i-1];
		}
	}
	
	public static String space(int num){
		return spaces[num];
	}
	
	public static String pad(String input, int size, boolean rightAlign){
		if(input.length() > size) 
			return input.substring(0,size);
		if(input.length() == size)
			return input;
		int numSpaces = size - input.length();
		if(rightAlign){
			return space(numSpaces) + input;
		} else {
			return input + space(numSpaces);
		}
		
	}
}
