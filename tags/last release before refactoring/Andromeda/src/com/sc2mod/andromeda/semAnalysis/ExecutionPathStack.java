/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.program.StrUtil;
import com.sc2mod.andromeda.syntaxNodes.Statement;

public class ExecutionPathStack {

	private Statement[] stmts = new Statement[512];
	private int[] frames = new int[128];
	private int curFrame = 0;
	private int curStmt = 0;
	
	public void pushFrame(){
		frames[curFrame] = curStmt;
		curFrame++;

		//System.out.println(StrUtil.space(curFrame) + "Frame pushed" + curFrame);
	}
	
	public void pushSingleStatementFrame(Statement s){
		pushFrame();
		pushStatement(s);
	}
	
	public void pushStatement(Statement s){

		stmts[curStmt] = s;
		curStmt++;

		//System.out.println(StrUtil.space(curFrame) + "Statement pushed : " + s);
	}
	
	public void popFrameAndConnect(Statement connectTo){
		int to = frames[--curFrame];
		while(curStmt>to){
			curStmt--;
			stmts[curStmt].setSuccessor(connectTo);
		}
		//System.out.println(StrUtil.space(curFrame) + "Popped and connected to : " + connectTo.getClass().getSimpleName());
		
	}

	public void mergeTopFrames() {
		curFrame--;

		//System.out.println(StrUtil.space(curFrame) +  "Merged");
	}

	public boolean isTopFrameEmpty() {
		return frames[curFrame-1] == curStmt;
	}

	public void popFrameAndDiscard() {
		curStmt = frames[--curFrame];

		//System.out.println(StrUtil.space(curFrame) + "Discarded");
	}
	
	public int getCurFrame(){
		return curFrame;
	}
	
	public void cutToFrame(int frameId){
		
		curFrame = frameId;
		curStmt = frames[frameId];
		//System.out.println(StrUtil.space(curFrame) + "Cut");
	}

	public boolean isStackEmpty(){
		return curFrame == 0&&curStmt == 0;
	}
}
