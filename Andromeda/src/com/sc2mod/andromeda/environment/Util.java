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

import com.sc2mod.andromeda.notifications.CompilationError;
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
				throw new CompilationError(a,"Unknown annotation '" + name + "' for " + annotatable.getDescription());
			}
			Annotation old = annotations.put(name, a);
			if(old != null){
				throw new CompilationError(a,"Unknown annotation '" + name + "' for " + annotatable.getDescription());
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
				if(m.isAbstract()) throw new CompilationError(mods,"Duplicate modifier 'abstract'.");
				m.setAbstract();
				break;
			case ModifierType.FINAL:
				if(m.isFinal()) throw new CompilationError(mods,"Duplicate modifier 'final'.");
				m.setFinal();
				break;
			case ModifierType.STATIC:
				if(m.isStatic()) throw new CompilationError(mods,"Duplicate modifier 'static'.");
				m.setStatic();
				break;
			case ModifierType.CONST:
				if(m.isConst()) throw new CompilationError(mods,"Duplicate modifier 'const'.");
				m.setConst();
				break;
			case ModifierType.OVERRIDE:
				if(m.isOverride()) throw new CompilationError(mods,"Duplicate modifier 'override'.");
				m.setOverride();
				break;
			case ModifierType.PRIVATE:
				if(m.getVisibility()!=Visibility.DEFAULT) if(m.isAbstract()) throw new CompilationError(mods,"Duplicate visibility modifier.");
				m.setVisibility(Visibility.PRIVATE);
				break;
			case ModifierType.PROTECTED:
				if(m.getVisibility()!=Visibility.DEFAULT) if(m.isAbstract()) throw new CompilationError(mods,"Duplicate visibility modifier.");
				m.setVisibility(Visibility.PROTECTED);
				break;
			case ModifierType.PUBLIC:
				if(m.getVisibility()!=Visibility.DEFAULT) if(m.isAbstract()) throw new CompilationError(mods,"Duplicate visibility modifier.");
				m.setVisibility(Visibility.PUBLIC);
				break;				
			case ModifierType.NATIVE:
				if(m.isNative()) throw new CompilationError(mods,"Duplicate modifier 'native'.");
				m.setNative();
				break;
			default:
				throw new CompilationError(mods,"Unknown modifier.");
			}
		}
	
	}
}
