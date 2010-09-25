package com.sc2mod.andromeda.util;

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
	
	private static String[] spaces = {
			"",
			"  ",
			"    ",
			"      ",
			"        ",
			"          "
	};
	
	public static String space(int num){
		return spaces[num];
	}
}
