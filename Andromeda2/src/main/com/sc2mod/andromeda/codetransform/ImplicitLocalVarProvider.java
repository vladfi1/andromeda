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

import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;

public class ImplicitLocalVarProvider {

	public ImplicitLocalVarProvider() {
		// TODO Auto-generated constructor stub
	}
	
	private ArrayList<FieldAccessExprNode> inUseBuffer = new ArrayList<FieldAccessExprNode>();
	private ArrayList<FieldAccessExprNode> methodBuffer = new ArrayList<FieldAccessExprNode>(); 
	private ArrayList<LocalVarDecl> methodDeclBuffer = new ArrayList<LocalVarDecl>(); 
	
	public FieldAccessExprNode getImplicitLocalVar(Type t){
		//Try to get one from the method buffer
		FieldAccessExprNode var = null;
		int size = methodBuffer.size();
		outer: for(int i=0; i < size; i++){
			FieldAccessExprNode v = methodBuffer.get(i);
			
			//Wrong type?
			if(v.getInferedType()!=t) continue;
			//Cannot take variables that are already in use
			for(FieldAccessExprNode v2: inUseBuffer){
				if(v == v2) continue outer;
			}
			
			//Got one!
			var = v;
			break;
		}
		
		//No var found? Create new one and add to method buffer
		if(var == null){
			String name = "L__".concat(String.valueOf(size));
			UninitedVarDeclNode vd = new UninitedVarDeclNode(new VarDeclNameNode(name, null));
			LocalVarDecl v = new LocalVarDecl(null, t, vd, true);
			

			//Get the user a name expression to this variable
			var = new FieldAccessExprNode(null,AccessTypeSE.SIMPLE,name);
			var.setSemantics(v);
			var.setInferedType(t);
			var.setSimple(true);
			methodBuffer.add(var);
			methodDeclBuffer.add(v);
		}
		
		//Var is in use now
		inUseBuffer.add(var);
		return var;
	}
	
	public void flushInUseLocals(){
		inUseBuffer.clear();
	}
	
	public void flushMethodBufferToFunction(Function f){
		f.addImplicitParams(methodDeclBuffer);
		methodBuffer.clear();
		methodDeclBuffer.clear();
	}
}
