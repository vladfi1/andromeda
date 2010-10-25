package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.codetransform.CallHierarchyVisitor;
import com.sc2mod.andromeda.codetransform.UnusedFinder;
import com.sc2mod.andromeda.codetransform.VirtualCallResolver;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;

public class CallHierarchyPhase extends Phase{

	public CallHierarchyPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Analyzing call hierarchy", true);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		Environment semEnv = env.getSemanticEnvironment();
		
		//Call hierarchy check
		System.out.print("Checking call hierarchy...");
		
		CallHierarchyVisitor chv = new CallHierarchyVisitor(env.getConfig());
		env.getSyntaxTree().accept(chv);
		
		VirtualCallResolver vcr = new VirtualCallResolver(env.getTransientData().getVirtualInvocations(),semEnv, chv);
		vcr.tryResolve();
		
		//XPilot: completes hierarchy analysis
		//chv.visitVirtualInvocations(env.getVirtualInvocations());
		
		//FIXME: Recode unused finder
		//UnusedFinder.process(env.getConfig(), semEnv);
	}

}
