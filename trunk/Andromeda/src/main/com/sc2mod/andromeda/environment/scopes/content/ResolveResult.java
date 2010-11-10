package com.sc2mod.andromeda.environment.scopes.content;

public enum ResolveResult {

	SUCCESSFUL,
	NOT_FOUND,
	NOT_VISIBLE,
	WRONG_SIGNATURE,
	DISALLOWED_TYPE,
	ACCESSOR_NOT_VISIBLE,
	ACCESSOR_WRONG_SIGNATURE;

	public ResolveResult toAccessorResult() {
		switch(this){
		case WRONG_SIGNATURE: return ACCESSOR_WRONG_SIGNATURE;
		case NOT_VISIBLE: return ACCESSOR_NOT_VISIBLE;
		}
		return this;
	}
}
