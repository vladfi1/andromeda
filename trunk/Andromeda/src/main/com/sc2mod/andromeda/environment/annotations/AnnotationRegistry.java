package com.sc2mod.andromeda.environment.annotations;

import java.util.HashMap;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationListNode;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;

public class AnnotationRegistry {

	private HashMap<String, IAnnotationType> registeredTypes = new HashMap<String, IAnnotationType>();
	
	public AnnotationRegistry(){
		//Add basic annotations
		for(IAnnotationType a : BasicAnnotations.getBasicAnnotationTypes()){
			registeredTypes.put(a.getName(), a);
		}
		
	}
	
	public void processAnnotationNode(IAnnotatable annotated, AnnotationNode annotation){
		String name = annotation.getName();
		
		//get annotation type and check that it exists
		IAnnotationType atype = registeredTypes.get(name);
		if(atype == null){
			Problem.ofType(ProblemId.UNKNOWN_ANNOTATION).at(annotation)
				.details(name)
				.raise();
			return;
		}
		
		//check that the annotation is allowed for this type
		if(!atype.checkAllowed(annotated)){
			Problem.ofType(ProblemId.ANNOTATION_NOT_ALLOWED).at(annotation)
				.details(name)
				.raise();
			return;
		}
		
		//get annotations and check for duplicate annotation.
		AnnotationSet set = annotated.getAnnotations(true);
		if(set.hasAnnotation(atype)){
			Problem.ofType(ProblemId.DUPLICATE_ANNOTATION).at(annotation,set.getAnnotation(atype).getDefinition())
				.details(name)
				.raise();
			return;
		}
		
		//Everything fine, add to annotation set
		set.addAnnotation(new Annotation(atype, annotation));
		
	}
	
	public void processAnnotations(IAnnotatable annotatable, AnnotationListNode al){
		if(al == null)
			return;
		for(int i = 0;i<al.size();i++){
			processAnnotationNode(annotatable, al.elementAt(i));
		}
	}
}
