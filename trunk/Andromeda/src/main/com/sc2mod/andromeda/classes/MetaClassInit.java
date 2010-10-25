/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes;

import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.parsing.TransientCompilationData;
import com.sc2mod.andromeda.semAnalysis.ResolveAndCheckTypesVisitor;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.BasicTypeNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

/**
 * Inits parameters for the metaClass "Class".
 * @author J. 'gex' Finis
 *
 */
public class MetaClassInit {

	public static void init(SyntaxGenerator sg, TransientCompilationData compilationData, Environment env) {
		TypeProvider tprov = env.typeProvider;
		if(tprov.getClasses().isEmpty()) return;
		
		Class metaClass = tprov.getSystemClass(AndromedaSystemTypes.T_CLASS);
		
		//We need as many meta classes as classes exist
		metaClass.setInstanceLimit(tprov.getClasses().size());
		
		//Add the virtual call table field		
		if(compilationData.getMaxVCTSize()>0){
			ArrayTypeNode at = new ArrayTypeNode(new BasicTypeNode("int"), new ExprListNode(sg.genIntLiteralExpr(compilationData.getMaxVCTSize())));
			VarDeclNode vdn;
			VarDeclListNode vd = new VarDeclListNode(vdn = new UninitedVarDeclNode(new IdentifierNode("vct")));
			FieldDeclNode fd = new FieldDeclNode(null, null, at, vd);
			FieldDecl fdecl = new FieldDecl(fd,vdn,metaClass, metaClass);
			metaClass.addContent("vct", fdecl);
			fdecl.accept(new ResolveAndCheckTypesVisitor(env));
			
		}
	}

}
