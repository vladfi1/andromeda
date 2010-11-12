package com.sc2mod.andromeda.environment.types.basic;

import java.util.ArrayList;
import java.util.Iterator;

import com.sc2mod.andromeda.environment.types.TypeProvider;

public class BasicTypeSet implements Iterable<BasicType> {

	public final BasicType STRING;
	public final BasicType INT;
	public final BasicType SHORT;
	public final BasicType BYTE;
	public final BasicType BOOL;
	public final BasicType CHAR;
	public final BasicType FLOAT;
	public final BasicType TEXT;
	
	public final SpecialType NULL;
	public final SpecialType VOID;
	
	private ArrayList<BasicType> basicTypeList = new ArrayList<BasicType>(40);
	
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
	
	public BasicTypeSet(TypeProvider tp){
		//Special types
		NULL = new TypeUnknown(tp);
		VOID = new SpecialType("void", tp);
		
		//Basic types
		STRING 	= addBasicTypeToList(new TypeString(tp));
		INT 	= addBasicTypeToList(new TypeInt(tp));
		SHORT 	= addBasicTypeToList(new TypeShort(tp));
		BYTE 	= addBasicTypeToList(new TypeByte(tp));
		BOOL 	= addBasicTypeToList(new TypeBool(tp));
		CHAR 	= addBasicTypeToList(new TypeChar(tp));
		FLOAT 	= addBasicTypeToList(new TypeFixed(tp));
		TEXT 	= addBasicTypeToList(new TypeText(tp));
		
		//Other basic types
		for(String s:additionalBasicTypes){
			addBasicTypeToList(new BasicType(s,tp));
		}
	}
	
	private BasicType addBasicTypeToList(BasicType t) {
		basicTypeList.add(t);
		return t;
	}

	@Override
	public Iterator<BasicType> iterator() {
		return basicTypeList.iterator();
	}
}
