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

import com.sc2mod.andromeda.environment.annotations.IAnnotatable;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationListNode;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;

public final class ModifierUtil {
	
	//FIXME modifiers into own set
	private ModifierUtil(){}
	
	public static void processModifiers(IModifiable m, ModifierListNode mods){
		if(mods==null) return;

		int size = mods.size();
		boolean visibilityWasThere = false;
		for(ModifierSE mod : mods){
			switch(mod){
			case ABSTRACT:
				if(m.isAbstract()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("abstract")
							.raise();
				else
					m.setAbstract();
				break;
			case FINAL:
				if(m.isFinal()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("final")
							.raise();
				else
					m.setFinal();
				break;
			case STATIC:
				if(m.isStatic()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("static")
							.raise();
				else
					m.setStatic();
				break;
			case CONST:
				if(m.isConst()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("const")
							.raise();
				else
					m.setConst();
				break;
			case OVERRIDE:
				if(m.isOverride()) 
					Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
							.details("override")
							.raise();
				else
					m.setOverride();
				break;
			case PRIVATE:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					m.setVisibility(Visibility.PRIVATE);
				visibilityWasThere = true;
				break;
			case PROTECTED:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					m.setVisibility(Visibility.PROTECTED);
				visibilityWasThere = true;
				break;
			case INTERNAL:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					m.setVisibility(Visibility.INTERNAL);
				visibilityWasThere = true;
				break;
			case PUBLIC:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else{
					Problem.raiseSingleTimeDeprecation(mods, 1337, "the 'public' modifier");
					m.setVisibility(Visibility.PUBLIC);
				}
				visibilityWasThere = true;
				break;				
			case NATIVE:
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
