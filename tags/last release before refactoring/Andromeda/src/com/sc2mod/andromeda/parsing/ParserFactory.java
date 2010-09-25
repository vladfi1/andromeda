package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.program.Options;

public class ParserFactory {



	private ParserLanguage lang;

	public ParserFactory(ParserLanguage lang) {
		this.lang = lang;
	}
	
	
	public Workflow createWorkflow(List<Source> files, Options o){
		if(lang == ParserLanguage.ANDROMEDA){
			return new AndromedaWorkflow(files,o);
		} else {
			return new GalaxyWorkflow(files,o);
		}
	}
}
