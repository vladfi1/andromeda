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

import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

/**
 * A variable stack is a datastructure that is able to register local
 * and global variables and then get the declaration of a variable
 * by its name.
 * Since I am not sure if an array with linear search or a hashmap
 * is better faster for this purpose, I have designed this as an abstract
 * class and will test both implementations as subclasses.
 * @author gex
 */
public class ArrayLocalVarStack extends LocalVarStack {

	private VarDecl[] stack = new VarDecl[256];
	private boolean[] visible = new boolean[256];
	private boolean[] inUse = new boolean[256];
	private int[] overrides = new int[256];
	private int[] framePointers = new int[64];
	private int[] varsInFrame = new int[64];
	private int stackPointer;
	private int numFrames;
	private int[] framesOnStack = new int[64];
	private int framePointer;
	
	public ArrayLocalVarStack() {
		reset();
		for(int i=0;i<256;i++){
			overrides[i] = -1;
		}
	}
	
	public void addLocalVar(VarDecl decl){
		
		String uid = decl.getUid();
		//Cache vars in locals for more speed
		VarDecl[] stack = this.stack;
		boolean[] visible = this.visible;
		
		//Check for duplicate locals and possible overrides
		for(int i= stackPointer-1;i>=0;i--){
			if(visible[i]){
				if(stack[i].getUid().equals(uid)) 
					throw Problem.ofType(ProblemId.DUPLICATE_LOCAL_VARIABLE)
						.at(decl.getDefinition(),stack[i].getDefinition())
						.details(decl.getUid())
						.raiseUnrecoverable();
			} else {
				//Can only override if the checked variable does not override already and shares the same type
				//and if it is not used currently
				if(!inUse[i]&&!stack[i].doesOverride()&&decl.getType()==stack[i].getType()){
					decl.override((LocalVarDecl)stack[i]);
					inUse[i] = true;
					overrides[stackPointer] = i;
				}
			}
		}
		
		visible[stackPointer] = true;
		stack[stackPointer++] = decl;
		varsInFrame[numFrames]++;
	}
	
	public void pushLocalBlock(){
		framesOnStack[framePointer++] = numFrames;
		framePointers[numFrames] = stackPointer;
		numFrames++;
		varsInFrame[numFrames] = 0;
	}
	
	public void popLocalBlock(){
		//Make all vars from this block invisible
		int frameId = framesOnStack[--framePointer];
		int numVars = varsInFrame[frameId+1];
		int pointer = framePointers[frameId];
		for(int i=0;i<numVars;i++){
			int override = overrides[pointer];
			if(override!=-1){
				inUse[override] = false;
				overrides[pointer]=-1;
			}
			visible[pointer++]=false;
		}
	}
	
	public VarDecl resolveVar(String name){
		for(int i= stackPointer-1;i>=0;i--){
			if(visible[i]&&stack[i].getUid().equals(name)) return stack[i];
		}
		return null;
	}
	
	private void reset(){
		for(int i= stackPointer-1;i>=0;i--){
			stack[i] = null;
		}
		stackPointer = 0;
		numFrames = 0;
		varsInFrame[0] = 0;
		framePointer = 0;
	}
	
	public LocalVarDecl[] methodFinished(int numParams){
		int vis=0; 
		for(int i=numParams,sp=stackPointer;i<sp;i++){
			if(!stack[i].doesOverride())vis++;
		}
		LocalVarDecl[] result = new LocalVarDecl[vis];
		vis=0;
		for(int i=numParams,sp=stackPointer;i<sp;i++){
			if(!stack[i].doesOverride()){
				result[vis++]=(LocalVarDecl)stack[i];
			}
		}
		reset();
		return result;
	}
}
