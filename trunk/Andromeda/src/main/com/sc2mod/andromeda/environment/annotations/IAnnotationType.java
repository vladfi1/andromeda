package com.sc2mod.andromeda.environment.annotations;


public interface IAnnotationType {
	
	String getName();

	
	/**
	 * Checks if this annoation type can be used onto the given annotabel.
	 * 
	 * If the method returns false, then a problem will be risen.
	 * 
	 * @param s
	 * @return
	 */
	boolean checkAllowed(IAnnotatable s);
}
