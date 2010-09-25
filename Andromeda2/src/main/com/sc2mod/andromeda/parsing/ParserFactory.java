package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.phases.InputCollectionPhase;
import com.sc2mod.andromeda.parsing.phases.ParsePhase;

public class ParserFactory {



	private Language lang;

	public ParserFactory(Language lang) {
		this.lang = lang;
	}
	
	
	public Workflow createWorkflow(List<Source> files, Configuration o){
		return new Workflow(
				files,o,
				new InputCollectionPhase(lang),
				new ParsePhase(lang)
			);
		
	}
}
