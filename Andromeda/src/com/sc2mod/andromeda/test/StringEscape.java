package com.sc2mod.andromeda.test;

/**
 * Re-escapes strings.
 * @author XPilot
 */
public class StringEscape {
	public static void main(String[] args) {
		String s = "//";
		test(s);
	}
	
	public static void test(String s) {
		System.out.println(s);
		System.out.println(reEscape(s));
	}
	
	public static String reEscape(String s) {
		StringBuffer buffer = new StringBuffer(s.length());
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
			case '\n': buffer.append("\\n");
				break;
			case '\r': buffer.append("\\r");
				break;
			case '\t': buffer.append("\\t");
				break;
			case '\f': buffer.append("\\f");
				break;
			case '\b': buffer.append("\\b");
				break;
			case '\"': case '\\': buffer.append('\\');
			default: buffer.append(c);
			}
		}
		return buffer.toString();
	}
}
