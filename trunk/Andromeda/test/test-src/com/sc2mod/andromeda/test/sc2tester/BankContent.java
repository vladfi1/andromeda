package com.sc2mod.andromeda.test.sc2tester;

import java.util.HashMap;

public class BankContent extends HashMap<String, BankSection>{

	public String getValue(String section, String key){
		BankSection sect = get(section);
		if(sect == null)
			return null;
		return sect.get(key);
	}
}
