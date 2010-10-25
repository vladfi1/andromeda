package com.sc2mod.andromeda.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

import com.sc2mod.andromeda.parsing.StringBufferWriter;

public class Debug {

	
	public static StackTraceElement[] getStackTrace(int offset, int depth){
		try {
			throw  new Exception();
		} catch (Exception e){
			StackTraceElement[] elems = e.getStackTrace();
			StackTraceElement[] newElems = new StackTraceElement[elems.length-offset];
			System.arraycopy(elems, offset, newElems, 0, newElems.length);
			int len = newElems.length;
			if(depth > 0 && len > depth){
				elems = new StackTraceElement[depth];
				System.arraycopy(newElems, 0, elems, 0, depth);
				newElems = elems;
			}
			return newElems;
		}
	}
	
	public static String getStackTrace(Throwable t){
		final StringBuffer sb = new StringBuffer();
		t.printStackTrace(new PrintWriter(new Writer(){
			public void close() throws IOException {}
			public void flush() throws IOException {}
			public void write(char[] cbuf, int off, int len) throws IOException {
				sb.append(cbuf,off,len);
			}
		}));
		return sb.toString();
	}
	
    private static void printStackTrace(PrintStream s, int offset, int depth) {
        synchronized (s) {
            StackTraceElement[] trace = getStackTrace(offset,depth);
            s.print(formatStackTrace(trace));
        }
    }
    
    public static String formatStackTrace(StackTraceElement[] trace){
    	StringBuilder sb = new StringBuilder(1024);
        for (int i=0; i < trace.length; i++)
            sb.append("\tat " + trace[i] + "\n");
        return sb.toString();
    }
    
    
    
    public static void printStackTrace(int depth){
    	printStackTrace(System.err, 3,depth);
    }

}
