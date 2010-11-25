package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.Environment;

public class NoSystemTypes extends SystemTypes{

	public NoSystemTypes(Environment env, TypeProvider tp) {
		super(env, tp);
	}

	@Override
	protected void onResolveSystemTypes() {
	}

}
