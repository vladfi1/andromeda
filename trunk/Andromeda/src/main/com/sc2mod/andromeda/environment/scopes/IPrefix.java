package com.sc2mod.andromeda.environment.scopes;

/**
 * A prefix is anything that can stand in front of a dot.
 * @author gex
 *
 */
public interface IPrefix {

	public IScope getPrefixScope();
	
}
