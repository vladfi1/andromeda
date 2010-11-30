package com.sc2mod.andromeda.environment;

import java.util.Set;

import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;

public class ModifierSet {
	private int bitset = 0;
	private Visibility visibility = Visibility.DEFAULT;
	
	private static final AllowedModifiersProvider modProvider = new AllowedModifiersProvider();
	public static final ModifierSet EMPTY_SET = new ModifierSet(null, null);
	
	public static final ModifierSet create(SemanticsElement se, ModifierListNode mods){
		if(mods == null){
			return EMPTY_SET;
		}
		//TODO Use hash-cons here
		return new ModifierSet(se, mods);
	}
	
	private ModifierSet(SemanticsElement se, ModifierListNode mods){
		if(mods != null){
			processModifiers(mods, modProvider.provide(se));
		}
	}
	
	private void processModifiers(ModifierListNode mods, Set<ModifierSE> allowedModifiers){
		int bitmask;
		boolean visibilityWasThere = false;
		for(ModifierSE mod : mods){
			//Check if allowed
			if(!allowedModifiers.contains(mod)){
				Problem.ofType(ProblemId.INVALID_VISIBILITY_MODIFIER).at(mods)
					.details("This definition")
					.raise();
			}
			
			//Create bitmask
			bitmask = 1 << mod.ordinal();
			
			//Duplicate modifier check
			if((bitset & bitmask) != 0){
				Problem.ofType(ProblemId.DUPLICATE_MODIFIER).at(mods)
					.details(mod.name().toLowerCase())
					.raise();
			}
			
			//Add to bit set
			bitset |= bitmask;
						
			//Visibility modifier check
			switch(mod){
			case PRIVATE:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					visibility = Visibility.PRIVATE;
				visibilityWasThere = true;
				break;
			case PROTECTED:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					visibility = Visibility.PROTECTED;
				visibilityWasThere = true;
				break;
			case INTERNAL:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else
					visibility = Visibility.INTERNAL;
				visibilityWasThere = true;
				break;
			case PUBLIC:
				if(visibilityWasThere)
					Problem.ofType(ProblemId.DUPLICATE_VISIBILITY_MODIFIER).at(mods)
							.raise();
				else{
					Problem.raiseSingleTimeDeprecation(mods, 1337, "the 'public' modifier");
					visibility = Visibility.PUBLIC;
				}
				visibilityWasThere = true;
				break;				
			}
		}
	
	}
	
	public Visibility getVisibility(){
		return visibility;
	}
	
	private static final int ABSTRACT_MASK = 1 << ModifierSE.ABSTRACT.ordinal();
	private static final int CONST_MASK = 1 << ModifierSE.CONST.ordinal();
	private static final int FINAL_MASK = 1 << ModifierSE.FINAL.ordinal();
	private static final int NATIVE_MASK = 1 << ModifierSE.NATIVE.ordinal();
	private static final int OVERRIDE_MASK = 1 << ModifierSE.OVERRIDE.ordinal();
	private static final int STATIC_MASK = 1 << ModifierSE.STATIC.ordinal();
	//private static final int TRANSIENT_MASK = 1 << ModifierSE.TRANSIENT.ordinal();
	
	public boolean isConst(){
		return (bitset & CONST_MASK) != 0;
	}

	public boolean isAbstract(){
		return (bitset & ABSTRACT_MASK) != 0;
	}
	
	public boolean isFinal(){
		return (bitset & FINAL_MASK) != 0;
	}
	
	public boolean isStatic(){
		return (bitset & STATIC_MASK) != 0;
	}
	
	public boolean isNative(){
		return (bitset & NATIVE_MASK) != 0;	
	}
	
	public boolean isOverride(){
		return (bitset & OVERRIDE_MASK) != 0;
	}
}
