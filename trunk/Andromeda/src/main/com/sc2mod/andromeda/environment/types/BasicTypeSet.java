package com.sc2mod.andromeda.environment.types;

import java.util.ArrayList;

/**
 * Some day, this will be the class for basic types, not the static constants in the class BasicType.
 * 
 * But it is not yet ready.
 * @author gex
 *
 */
public class BasicTypeSet {

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
}
