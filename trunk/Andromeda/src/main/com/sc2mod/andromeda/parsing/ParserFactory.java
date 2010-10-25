package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.parsing.phases.CallHierarchyPhase;
import com.sc2mod.andromeda.parsing.phases.CodeGenPhase;
import com.sc2mod.andromeda.parsing.phases.CodeGenPreparationPhase;
import com.sc2mod.andromeda.parsing.phases.InputCollectionPhase;
import com.sc2mod.andromeda.parsing.phases.ParsePhase;
import com.sc2mod.andromeda.parsing.phases.Phase;
import com.sc2mod.andromeda.parsing.phases.PrintResultsPhase;
import com.sc2mod.andromeda.parsing.phases.RunMapPhase;
import com.sc2mod.andromeda.parsing.phases.SemanticAnalysisPhase;
import com.sc2mod.andromeda.parsing.phases.SimplificationPhase;
import com.sc2mod.andromeda.parsing.phases.WriteCodePhase;
import com.sc2mod.andromeda.parsing.phases.WriteXMLPhase;

public class ParserFactory {



	private Language lang;

	public ParserFactory(Language lang) {
		this.lang = lang;
	}
	
	
	public Workflow createWorkflow(List<Source> files, Configuration o){
		boolean genCode = !o.getParamBool(Parameter.MISC_NO_CODE_GEN);
		File xmlStructure = o.getParamFile(Parameter.FILES_XML_STRUCTURE);
		File xmlErrors = o.getParamFile(Parameter.FILES_XML_ERRORS);
		
		ArrayList<Phase> phases = new ArrayList<Phase>();
		phases.add(new InputCollectionPhase(lang));
		phases.add(new ParsePhase(lang));
		phases.add(new SemanticAnalysisPhase());
		if(genCode){
			phases.add(new SimplificationPhase());
			phases.add(new CallHierarchyPhase());
			phases.add(new CodeGenPreparationPhase());
			phases.add(new CodeGenPhase());
			phases.add(new WriteCodePhase());
		}
		if(xmlStructure != null || xmlErrors != null){
			phases.add(new WriteXMLPhase(xmlStructure, xmlErrors));
		}
		phases.add(new PrintResultsPhase());
		if(o.getParamBool(Parameter.TEST_RUN_MAP_AFTER_COMPILE)){
			phases.add(new RunMapPhase());
		}
		
		return new Workflow(
				files,o,
				phases.toArray(new Phase[phases.size()])
			);
		
	}
}
