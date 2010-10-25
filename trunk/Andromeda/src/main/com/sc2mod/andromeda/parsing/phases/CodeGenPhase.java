package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.indexSys.IndexClassGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.OutputMemoryStats;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;

public class CodeGenPhase extends Phase{

	public CodeGenPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Generating code", true);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		Environment semEnv = env.getSemanticEnvironment();
		Configuration options = env.getConfig();
		NameGenerationVisitor snv = env.getTransientData().getNameGenerator();
		CodeGenVisitor c = new CodeGenVisitor(semEnv, options,snv.getNameProvider());
		if(!semEnv.typeProvider.getClasses().isEmpty()) {
			ClassGenerator cg = new IndexClassGenerator(semEnv,c,snv.getNameProvider(), options);
			cg.generateClasses(semEnv.typeProvider.getClasses());
		}
		c.generateCode(snv,env.getSyntaxTree());
		c.writeInit(env.getSyntaxTree());
		
		env.getTransientData().setCodeGenerator(c);
		
	}

}
