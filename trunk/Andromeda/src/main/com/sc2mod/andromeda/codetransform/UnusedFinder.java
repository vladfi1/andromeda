/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codetransform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;

/**
 * Class for finding unused content
 * @author J. 'gex' Finis
 *
 */
//XPilot: no longer remove variables that are not written to
public class UnusedFinder {

	
	public static void process(Configuration options, Environment env){
		
		checkUnusedGlobals(options,env);
		checkUnusedFields(options,env);
		checkUncalledFunctions(options,env);
	}
	


	private static void handleUnusedFunction(Operation vd){
		Problem p = Problem.ofType(ProblemId.UNCALLED_FUNCTION).at(vd.getDefinition())
						.details(vd.getDescription())
						.raise();
		if(p.wantRemove()) vd.setCreateCode(false);
	}

	private static void checkFunction(Operation f, InclusionType inclusionType){
		//Strcall functions are never uncalled
		if(f.isStrcall()) return;
		//methods that are called virtually should not be omitted
		if(f.getInvocationCount()==0 /* && !(f instanceof Method && f.isCalledVirtually()) */) {
			boolean isLib;
			
			switch(inclusionType){
			case LANGUAGE:
			case NATIVE:
				return;
			case LIBRARY:
				isLib = true;
				break;
			default:
				isLib = false;
			}
			if(isLib){
				//System.out.println(f.getDescription());
				f.setCreateCode(false);
			} else {
				handleUnusedFunction(f);
			}
		}
	}
	private static int handleUnusedFunctions;
	private static void checkUncalledFunctions(Configuration options, Environment env) {
		//Functions
		for(Entry<String, LinkedHashMap<Signature, LinkedList<Function>>> e : env.getFunctions().getFunctionTable().entrySet()){
			for(Entry<Signature, LinkedList<Function>> e2: e.getValue().entrySet()){
				for(Function f: e2.getValue()){
					checkFunction(f,f.getScope().getInclusionType());
				}
			}
		}
		
		//Methods
		for(RecordType r: env.typeProvider.getRecordTypes()){
			InclusionType inclusionType = r.getScope().getInclusionType();
			if(inclusionType==InclusionType.NATIVE) continue;
			LinkedHashMap<String, LinkedHashMap<Signature, Operation>> methods = r.getMethods().getMethodTable();
			for(Entry<String, LinkedHashMap<Signature, Operation>> meths: methods.entrySet()){
				for(Entry<Signature, Operation> keyvalue: meths.getValue().entrySet()){
					checkFunction(keyvalue.getValue(),inclusionType);
				}
			}
		}
	}


	
	static void checkForUnusedLocals(Function f, Configuration options){
		//Check for unused locals
		LocalVarDecl[] locals = f.getLocals();
		if(locals == null) return;
		for(LocalVarDecl vd : locals){
			if(vd.getNumReadAccesses()==0){
				Problem.ofType(ProblemId.UNREAD_VARIABLE).at(vd.getDefinition())
						.details("local variable", vd.getUid())
						.raise();
			}
			else if(vd.getNumWriteAccesses()==0){
				Problem.ofType(ProblemId.UNWRITTEN_VARIABLE).at(vd.getDefinition())
				.details("local variable", vd.getUid())
				.raise();
			}
		}
	}
	
	private static void checkUnusedGlobals(Configuration options, Environment env){
		LinkedHashMap<String, ArrayList<GlobalVarDecl>> variables = env.getGlobalVariables().getVarSet();
		 
		for(Entry<String, ArrayList<GlobalVarDecl>> e: variables.entrySet()){
			for(GlobalVarDecl decl: e.getValue()){
				
				InclusionType inclusionType = decl.getScope().getInclusionType();
				boolean isLib;
				switch(inclusionType){
				case LANGUAGE:
				case NATIVE:
					continue;
				case LIBRARY:
					isLib = true;
					break;
				default:
					isLib = false;
				}

				//System.out.println(decl.getGeneratedName());
				
				//XPilot: don't remove a variable if it is written to
				if(decl.getNumReadAccesses()==0 && decl.getNumReadAccesses() == 0){
					if(isLib||decl.getNumInlines()>0){
						decl.setCreateCode(false);
					} else {
						Problem p = Problem.ofType(ProblemId.UNREAD_VARIABLE).at(decl.getDefinition())
										.details("global variable", decl.getUid())
										.raise();
						if(p.wantRemove())decl.setCreateCode(false);
					}
				} else if(decl.getNumWriteAccesses()==0&&decl.getType().getCategory()!=com.sc2mod.andromeda.environment.types.Type.ARRAY){
					Problem.ofType(ProblemId.UNINITIALIZED_VARIABLE).at(decl.getDefinition())
							.details("global variable", decl.getUid())
							.raise();
				}
				
			}
		}
	}
	
	private static void checkUnusedFields(Configuration options, Environment env) {

		//Fields
		for(RecordType r: env.typeProvider.getRecordTypes()){
			InclusionType inclusionType = r.getScope().getInclusionType();
			boolean isLib;
			switch(inclusionType){
			case LANGUAGE:
			case NATIVE:
				continue;
			case LIBRARY:
				isLib = true;
				break;
			default:
				isLib = false;
			}
			
			FieldSet fi = r.getFields();
			ArrayList<FieldDecl> fields = fi.getStaticClassFields();
			for(FieldDecl decl: fields){
				//XPilot: don't remove a variable if it is written to
				if(decl.getNumReadAccesses()==0 && decl.getNumWriteAccesses() == 0){
					if(isLib|| decl.getNumInlines()>0){
						decl.setCreateCode(false);
					} else {
						Problem p = Problem.ofType(ProblemId.UNREAD_VARIABLE).at(decl.getDefinition())
							.details("static field", decl.getUid())
							.raise();
						if(p.wantRemove())decl.setCreateCode(false);
					}
				} else if(decl.getNumWriteAccesses()==0){
					Problem.ofType(ProblemId.UNINITIALIZED_VARIABLE).at(decl.getDefinition())
						.details("static field", decl.getUid())
						.raise();
				}
			}
			

			fields = fi.getNonStaticClassFields();
			for(FieldDecl decl: fields){
				//XPilot: don't remove a variable if it is written to
				if(decl.getNumReadAccesses()==0 && decl.getNumWriteAccesses() == 0){
					if(isLib){
						//Fields cannot be removed, so do nothing in a lib
					} else {
						Problem.ofType(ProblemId.UNREAD_VARIABLE).at(decl.getDefinition())
							.details("field", decl.getUid())
							.raise();
					}
				} else if(decl.getNumWriteAccesses()==0){
					//XPilot: containing class may not be used
					if(decl.getContainingType().isClass() && ((Class)decl.getContainingType()).isUsed())
						
						Problem.ofType(ProblemId.UNINITIALIZED_VARIABLE).at(decl.getDefinition())
							.details("field", decl.getUid())
							.raise();
				}
			}
		}
	}
}
