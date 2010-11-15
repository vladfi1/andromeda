package com.sc2mod.andromeda.environment.annotations;

import java.util.ArrayList;
import java.util.Arrays;

public enum BasicAnnotations implements IAnnotationType {
	
	KEEP_AFTER_FOREACH("KeepAfterForeach"),
	INLINE("Inline");
	
	private String name;
	
	private BasicAnnotations(String name){
		this.name = name;
	}

	public static Iterable<BasicAnnotations> getBasicAnnotationTypes(){
		return Arrays.asList(values());
	}

	@Override
	public boolean checkAllowed(IAnnotatable s) {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}
}
