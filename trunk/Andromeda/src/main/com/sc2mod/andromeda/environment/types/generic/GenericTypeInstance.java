package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.impl.NamedTypeImpl;

public abstract class GenericTypeInstance extends NamedTypeImpl {

	protected GenericTypeInstance(NamedTypeImpl genericParent, Signature s) {
		super(genericParent, s);
	}

}
