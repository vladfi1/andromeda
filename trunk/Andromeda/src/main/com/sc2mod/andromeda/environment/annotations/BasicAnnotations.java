package com.sc2mod.andromeda.environment.annotations;

public enum BasicAnnotations implements IAnnotationType {
	
	KEEP_AFTER_FOREACH, INLINE;

	public static Iterable<IAnnotationType> getBasicAnnotationTypes(){
		return null;
	}

	@Override
	public boolean checkAllowed(IAnnotatable s) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
}
