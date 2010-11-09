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

import com.sc2mod.andromeda.environment.access.VarAccess;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;

public class ImplicitLocalVarProvider {

	public ImplicitLocalVarProvider() {
		// TODO Auto-generated constructor stub
	}
	
	private ArrayList<NameExprNode> inUseBuffer = new ArrayList<NameExprNode>();
	private ArrayList<NameExprNode> methodBuffer = new ArrayList<NameExprNode>(); 
	private ArrayList<LocalVarDecl> methodDeclBuffer = new ArrayList<LocalVarDecl>(); 
	
	public NameExprNode getImplicitLocalVar(IType t){
		//Try to get one from the method buffer
		NameExprNode var = null;
		int size = methodBuffer.size();
		outer: for(int i=0; i < size; i++){
			NameExprNode v = methodBuffer.get(i);
			
			//Wrong type?
			if(v.getInferedType()!=t) continue;
			//Cannot take variables that are already in use
			for(NameExprNode v2: inUseBuffer){
				if(v == v2) continue outer;
			}
			
			//Got one!
			var = v;
			break;
		}
		
		//No var found? Create new one and add to method buffer
		if(var == null){
			String name = "L__".concat(String.valueOf(size));
			UninitedVarDeclNode vd = new UninitedVarDeclNode(new IdentifierNode(name));
			LocalVarDecl v = new LocalVarDecl(null, t, vd, true, null);
			

			//Get the user a name expression to this variable
			var = new NameExprNode(name);
			var.setSemantics(new VarAccess(v));
			var.setInferedType(t);
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
		f.addImplicitLocals(methodDeclBuffer);
		methodBuffer.clear();
		methodDeclBuffer.clear();
	}
}
