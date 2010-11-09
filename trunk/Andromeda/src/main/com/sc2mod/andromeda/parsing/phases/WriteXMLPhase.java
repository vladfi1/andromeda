package com.sc2mod.andromeda.parsing.phases;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.xml.gen.ResultXMLWriter;
import com.sc2mod.andromeda.xml.gen.StructureXMLVisitor;

public class WriteXMLPhase extends Phase{

	private File xmlStructure;
	private File xmlResult;

	public WriteXMLPhase(File xmlStructure, File xmlResult) {
		super(PhaseRunPolicy.ALWAYS, "Writing XML output", true);
		this.xmlStructure = xmlStructure;
		this.xmlResult = xmlResult;
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		Configuration options = env.getConfig();
		
		//Output structure to xml if desired to
		if(xmlStructure != null) {
			try {
				new StructureXMLVisitor(options).genXml(env.getFileManager(), xmlStructure, env.getSyntaxTree());
			} catch (XMLStreamException e) {
				ErrorUtil.raiseExternalProblem(e, false);
			} catch (IOException e) {
				ErrorUtil.raiseIOProblem(e, false);
			}
		}	
		
		//Output result to xml
		if(xmlResult != null) {
			try {
				new ResultXMLWriter().genXML(env.getResult(), xmlResult);
			} catch (XMLStreamException e) {
				ErrorUtil.raiseExternalProblem(e, false);
			} catch (IOException e) {
				ErrorUtil.raiseIOProblem(e, false);
			}
		}
	}

}
