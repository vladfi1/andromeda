/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.Annotation;
import com.sc2mod.andromeda.syntaxNodes.AnnotationList;
import com.sc2mod.andromeda.syntaxNodes.ModifierType;
import com.sc2mod.andromeda.syntaxNodes.Modifiers;

public final class Util {

	private Util(){}
	
	public static void processAnnotations(IAnnotatable annotatable, AnnotationList al){
		if(al==null) return;
		HashMap<String, Annotation> annotations = new HashMap<String, Annotation>();
		HashSet<String> allowedAnnotations = annotatable.getAllowedAnnotations();
		annotatable.setAnnotationTable(annotations);
		int size = al.size();
		for(int i=0; i<size; i++){
			Annotation a = al.elementAt(i);
			String name = a.getName();
			if(!allowedAnnotations.contains(name)){
				throw Problem.ofType(ProblemId.UNKNOWN_ANNOTATION).at(a)
						.details(name,annotatable.getDescription())
						.raiseUnrecoverable();
			}
			Annotation old = annotations.put(name, a);
			if(old != null){
				throw Problem.ofType(ProblemId.UNKNOWN_ANNOTATION).at(a)
				.details(name,annotatable.getDescription())
				.raiseUnrecoverable();
			}
		}
		annotatable.afterAnnotationsProcessed();
	}
	
	public static void processModifiers(IModifiable m, Modifiers mods){
		if(mods==null) return;
		int size = mods.size();
		for(int i=0;i<size;i++){
			switch(mods.elementAt(i)){
			case ModifierType.ABSTRACT:
				if(m.isAbstract()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("abstract")
							.raise();
				else
					m.setAbstract();
				break;
			case ModifierType.FINAL:
				if(m.isFinal()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("final")
							.raise();
				else
					m.setFinal();
				break;
			case ModifierType.STATIC:
				if(m.isStatic()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("static")
							.raise();
				else
					m.setStatic();
				break;
			case ModifierType.CONST:
				if(m.isConst()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("const")
							.raise();
				else
					m.setConst();
				break;
			case ModifierType.OVERRIDE:
				if(m.isOverride()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("override")
							.raise();
				else
					m.setOverride();
				break;
			case ModifierType.PRIVATE:
				if(m.getVisibility()!=Visibility.DEFAULT)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					m.setVisibility(Visibility.PRIVATE);
				break;
			case ModifierType.PROTECTED:
				if(m.getVisibility()!=Visibility.DEFAULT)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					m.setVisibility(Visibility.PROTECTED);
				break;
			case ModifierType.PUBLIC:
				if(m.getVisibility()!=Visibility.DEFAULT)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else 
					m.setVisibility(Visibility.PUBLIC);
				break;				
			case ModifierType.NATIVE:
				if(m.isNative()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("native")
							.raise();
				else
					m.setNative();
				break;
			default:
				throw new InternalProgramError(mods,"Unknown modifier.");
			}
		}
	
	}
}
