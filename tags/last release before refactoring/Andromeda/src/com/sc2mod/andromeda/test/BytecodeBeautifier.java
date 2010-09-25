/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.test;

public class BytecodeBeautifier {

	private static final int WHITESPACE = 14;

	private static String[] table = new String[64];
	
	private static String[] register = new String[17];
	static{
		register[1] = "1";
		register[2] = "2";
		register[4] = "3";
		register[8] = "4";
		register[16] = "5";
		table[0x00] = "ADD INT";
		table[0x31] = "WRITE TO";
		//Add additional opcodes here (but use the real opcode, not the bytes you see!		
	}
	
	StringBuffer buffer = new StringBuffer(32);
	
	private void writeHeader(){
		buffer.append("OpCode");
		appendWhitespace(WHITESPACE-buffer.length());
		buffer.append("R1 R2");
		System.out.println(buffer.toString());
		buffer.setLength(0);	
	}
	
	/**
	 * Call this method with the string you want to get parsed.
	 * The result is shown on the standard out.
	 * @param s the string to parse
	 */
	public void parseStr(String s){
		
		writeHeader();
		
		
		char[] chars = s.toCharArray();
		int size = chars.length;
		int code = 0;
		int digit = 7;
		for(int i=0;i<size;i++){
			char c = chars[i];
			
			int val;
			if(c >= 48 && c <= 57){
				val = c - 48;
			} else if(c >= 65 && c <= 70){
				val = c - 55;
			} else continue;
			
			code |= val << (4*digit);
			digit--;
			if(digit<0){
				opFound(Integer.reverseBytes(code));
				digit = 7;
				code = 0;
			}
			
		}
	}

	private void appendWhitespace(int num){
		for(int i=num;i>0;i--){
			buffer.append(" ");
		}
	}

	private void opFound(int code) {
		int opCode = (code&0xFC000000)>>>26;
		int register1 = (code&0x03E00000)>>>21;
		int register2 = (code&0x001F0000)>>>16;
		
		String s = table[opCode];
		if(s==null)s = "unknown (" + opCode + ")";
		buffer.append(s);
		appendWhitespace(WHITESPACE-buffer.length());
		buffer.append(register1).append("  ").append(register2);
		System.out.println(buffer.toString());
		buffer.setLength(0);
	}
	
	public static void main(String[] args){
		new BytecodeBeautifier().parseStr("10 00 41 C4 00 00 00 06");
	}
}
