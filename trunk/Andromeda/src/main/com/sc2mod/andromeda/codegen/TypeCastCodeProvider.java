package com.sc2mod.andromeda.codegen;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;

public class TypeCastCodeProvider {

	public TypeCastCodeProvider(TypeProvider tp) {
		init(tp.BASIC);
	}
	
	private HashMap<IType,HashMap<IType,StringPair>> typeCasts = new HashMap<IType,HashMap<IType,StringPair>>();
	
	private void init(BasicTypeSet BASIC){
		HashMap<IType,StringPair> cur;
		//Casts from fixed
		typeCasts.put(BASIC.FLOAT, cur = new HashMap<IType, StringPair>());
		cur.put(BASIC.INT, new StringPair("FixedToInt(",")"));
		cur.put(BASIC.BYTE, new StringPair("(FixedToInt(",")&0xff)"));
		cur.put(BASIC.STRING, new StringPair("FixedToString(",",3)"));
		cur.put(BASIC.TEXT, new StringPair("FixedToText(",",3)"));
		cur.put(BASIC.BOOL, new StringPair("(","!=0.0)"));
		
		//Casts from int
		typeCasts.put(BASIC.INT, cur = new HashMap<IType, StringPair>());
		cur.put(BASIC.BYTE, new StringPair("((",")&0xff)"));
		cur.put(BASIC.FLOAT, new StringPair("IntToFixed(",")"));
		cur.put(BASIC.STRING, new StringPair("IntToString(",")"));
		cur.put(BASIC.TEXT, new StringPair("IntToText(",")"));
		cur.put(BASIC.BOOL, new StringPair("(","!=0)"));
		
		//Casts from string
		typeCasts.put(BASIC.STRING, cur = new HashMap<IType, StringPair>());
		cur.put(BASIC.FLOAT, new StringPair("StringToFixed(",")"));
		cur.put(BASIC.INT, new StringPair("StringToInt(",")"));
		cur.put(BASIC.BYTE, new StringPair("(StringToInt(",")&0xff)"));
		cur.put(BASIC.TEXT, new StringPair("StringToText(",")"));
		cur.put(BASIC.BOOL, new StringPair("(","!=null)"));
		
		//Casts from text
		typeCasts.put(BASIC.TEXT, cur = new HashMap<IType, StringPair>());
		cur.put(BASIC.FLOAT, new StringPair("StringToFixed(TextToString(","))"));
		cur.put(BASIC.INT, new StringPair("StringToInt(TextToString(","))"));
		cur.put(BASIC.STRING, new StringPair("TextToString(",")"));
		cur.put(BASIC.BOOL, new StringPair("(","!=null)"));
		
		//XPilot: Casts from bool?
	}
	
	public StringPair getCastOp(IType fromType, IType toType){
		HashMap<IType, StringPair> tcs = typeCasts.get(fromType);
		if(tcs==null) return null;
		return tcs.get(toType);
	}
}
