package com.sc2mod.andromeda.environment;

public class IdProvider {
	private int curStructId = 0;
	private int curGlobalVarId = 0;
	
	public int getNextStructId(){
		return ++curStructId;
	}
	
	public int getNextGlobalVarId(){
		return ++curGlobalVarId;
	}
}
