package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.classes.ClassFieldCalculator;
import com.sc2mod.andromeda.classes.MetaClassInit;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassFieldCalculator;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.TypeIndexGenerator;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;

public class CodeGenPreparationPhase extends Phase{

	public CodeGenPreparationPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Preparing code generation", true);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		Environment semEnv = env.getSemanticEnvironment();
		Configuration options = env.getConfig();
		
		//Generate indices
		env.getTransientData().setIndexInformation(
				new TypeIndexGenerator().createIndexInformation(semEnv)
			);
		
		//Generate virtual call tables
		VirtualCallTable.generateVCTs(env.getTransientData(),semEnv);
		
		//Initialize meta class
		MetaClassInit.init(new SyntaxGenerator(semEnv.typeProvider),env.getTransientData(),semEnv);
		
		//Generate and save name provider
		INameProvider nameProvider = INameProvider.Factory.createProvider(env, options);
	
		//Do name and class field generation
		NameGenerationVisitor snv = new NameGenerationVisitor(semEnv.typeProvider, nameProvider, options);
		env.getTransientData().setNameGenerator(snv);
		ClassFieldCalculator cfc = new IndexClassFieldCalculator(semEnv.typeProvider,snv.getNameProvider(), env.getTransientData().getIndexInformation());
		cfc.calcFields();
		semEnv.typeProvider.calcFuncPointerIndices();
		cfc.generateClassNames();
		snv.prepareNameGeneration();
		env.getSyntaxTree().accept(snv);
		
		
	}
	
	

}
