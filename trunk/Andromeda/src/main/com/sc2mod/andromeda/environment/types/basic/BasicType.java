/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.NamedTypeImpl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class BasicType extends NamedTypeImpl {
	
	
	
	private static ArrayList<BasicType> basicTypeList = new ArrayList<BasicType>(35);

	public static final BasicType STRING = new TypeString();
	public static final BasicType INT = new TypeInt();
	public static final BasicType SHORT = new TypeShort();
	public static final BasicType BYTE = new TypeByte();
	public static final BasicType BOOL = new TypeBool();
	public static final BasicType CHAR = new TypeChar();
	public static final BasicType FLOAT = new TypeFixed();
	public static final BasicType TEXT = new TypeText();
	private static final String[] additionalBasicTypes = 
				{
					"abilcmd",
					"actor",
				    "actorscope",
				    "aifilter",
				    "animtarget",
				    "bank",
				    "camerainfo",
				    "color",
				    "doodad",
				    "handle",
				    "marker",
				    "order",
				    "playergroup",
				    "point",
				    "region",
				    "revealer",
				    "sound",
				    "soundlink",
				    "timer",
				    "transmissionsource",
				    "trigger",
				    "unit",
				    "unitfilter",
				    "unitgroup",
				    "unitref",
				    "wave",
				    "waveinfo",
				    "wavetarget"
				};

    

	@Override
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	public static ArrayList<BasicType> getBasicTypeList() {
		return basicTypeList;
	}


	public BasicType(String name) {
		this(null,name); //Basic types have no scope
	}
	
	/**
	 * Constructor for types that do have a scope.
	 * @param scope
	 */
	protected BasicType(IScope scope, String name){
		super(scope,name);
		if(this.getCategory()==TypeCategory.BASIC)
			basicTypeList.add(this);
	}
	
	/**
	 * Constructor for generic instances of a type.
	 * @param genericParent the type for which to create a generic instance.
	 */
	protected BasicType(BasicType genericParent, Signature sig){
		super(genericParent,sig);
	}
	

	@Override
	public boolean canBeNull() {
		return true;
	}

	/**
	 * registers all basic types of galaxy / andromeda
	 * which do not have to be defined anywhere
	 * @param t
	 */
	public static void registerBasicTypes(TypeProvider t) {
		t.registerBasicType(STRING);
		t.registerBasicType(INT);
		t.registerBasicType(SHORT);
		t.registerBasicType(BOOL);
		t.registerBasicType(BYTE);
		t.registerBasicType(CHAR);
		t.registerBasicType(FLOAT);
		t.registerBasicType(TEXT);
		for(String s:additionalBasicTypes){
			t.registerBasicType(new BasicType(s));
		}
		SpecialType.registerSpecialTypes(t);
	}
	

	

	@Override
	public String getDescription() {
		return "native type";
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.BASIC;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}


	
	

	@Override
	public Visibility getVisibility() {
		return Visibility.PUBLIC;
	}

	@Override
	public SyntaxNode getDefinition() {
		return null;
	}


	@Override
	public INamedType createGenericInstance(Signature s) {
		throw new Error("Cannot create a generic instance of a basic type!");
	}


	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
