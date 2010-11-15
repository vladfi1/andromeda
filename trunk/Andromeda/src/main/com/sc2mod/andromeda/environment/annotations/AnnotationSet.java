package com.sc2mod.andromeda.environment.annotations;

import java.util.HashMap;

public class AnnotationSet {
	
	private HashMap<IAnnotationType, Annotation> table = new HashMap<IAnnotationType, Annotation>();

	public boolean hasAnnotation(IAnnotationType at){
		return table.containsKey(at);
	}
	
	public Annotation getAnnotation(IAnnotationType at){
		return table.get(at);
	}
	
	public void addAnnotation(Annotation a){
		table.put(a.getAnnotationType(), a);
	}
}
