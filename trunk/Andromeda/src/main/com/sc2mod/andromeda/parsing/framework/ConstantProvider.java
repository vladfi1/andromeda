package com.sc2mod.andromeda.parsing.framework;

import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.CharObject;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;
import com.sc2mod.andromeda.vm.data.NullObject;
import com.sc2mod.andromeda.vm.data.StringObject;

public class ConstantProvider {

	public static NullObject getNullObject(){
		return NullObject.INSTANCE;
	}
	
	public static StringObject getStringObject(String s){
		return new StringObject(s);
	}
	
	public static CharObject getCharObject(char c){
		return new CharObject(c);
	}
	
	public static BoolObject getBoolObject(boolean b){
		return BoolObject.getBool(b);
	}
	
	public static FixedObject getFixedObject(Number n){
		return new FixedObject(n);
	}
	
	public static IntObject getIntObject(int i){
		return new IntObject(i);
	}
}
