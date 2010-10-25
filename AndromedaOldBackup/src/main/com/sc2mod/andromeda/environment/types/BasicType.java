/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

import java.util.ArrayList;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;


public class BasicType extends SimpleType {
	
	
	
	private static ArrayList<BasicType> basicTypeList = new ArrayList<BasicType>(35);
	
	protected int curKey = 1;
	
	public static final BasicType STRING = new TypeString();
	public static final BasicType INT = new TypeInt();
	public static final BasicType SHORT = new TypeShort();
	public static final BasicType BYTE = new TypeByte();
	public static final BasicType BOOL = new TypeBool();
	public static final BasicType CHAR = new TypeChar();
	public static final BasicType FLOAT = new TypeFixed();
	public static final BasicType TEXT = new BasicType("text");
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

    
	
	private String name;

	@Override
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	public static ArrayList<BasicType> getBasicTypeList() {
		return basicTypeList;
	}


	public BasicType(String name) {
		super();
		if(this.getCategory()==BASIC)
			basicTypeList.add(this);
		this.name = name;
	}
	

	@Override
	public boolean canBeNull() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * registers all basic types of galaxy / andromeda
	 * which do not have to be defined anywhere
	 * @param t
	 */
	static void registerBasicTypes(TypeProvider t) {
		t.registerSimpleType(STRING);
		t.registerSimpleType(INT);
		t.registerSimpleType(SHORT);
		t.registerSimpleType(BOOL);
		t.registerSimpleType(BYTE);
		t.registerSimpleType(CHAR);
		t.registerSimpleType(FLOAT);
		t.registerSimpleType(TEXT);
		for(String s:additionalBasicTypes){
			t.registerSimpleType(new BasicType(s));
		}
		SpecialType.registerSpecialTypes(t);
	}
	
	@Override
	public boolean canExplicitCastTo(Type toType) {
		if(toType == this) return true;
		if(toType.getCategory()==EXTENSION){
			return toType.getBaseType()==this;
		}
		return false;
	}
	

	@Override
	public String getDescription() {
		return "native type";
	}

	@Override
	public String getUid() {
		return name;
	}

	@Override
	public int getCategory() {
		return BASIC;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}


	
	
}
