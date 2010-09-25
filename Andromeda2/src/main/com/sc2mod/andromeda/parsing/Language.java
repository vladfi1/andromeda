package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.parsing.andromeda.Andromeda;
import com.sc2mod.andromeda.parsing.galaxy.Galaxy;

public enum Language {
		ANDROMEDA(new Andromeda()),
		GALAXY(new Galaxy());
		
		private LanguageImpl impl;
		
		private Language(LanguageImpl li){
			this.impl = li;
		}

		public LanguageImpl getImpl() {
			return impl;
		}
	
}
