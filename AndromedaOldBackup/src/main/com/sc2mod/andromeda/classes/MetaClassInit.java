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
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.BasicTypeNode;
import com.sc2mod.andromeda.syntaxNodes.MemberTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeCategorySE;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;

/**
 * Inits parameters for the metaClass "Class".
 * @author J. 'gex' Finis
 *
 */
public class MetaClassInit {

	public static void init(SyntaxGenerator sg, Environment env) {
		TypeProvider tprov = env.typeProvider;
		if(tprov.getClasses().isEmpty()) return;
		
		Class metaClass = tprov.getSystemClass(AndromedaSystemTypes.T_CLASS);
		
		//We need as many meta classes as classes exist
		metaClass.setInstanceLimit(tprov.getClasses().size());
		
		//Add the virtual call table field		
		if(env.getMaxVCTSize()>0){
			ArrayTypeNode at = new ArrayTypeNode(TypeCategorySE.ARRAY, new BasicTypeNode(TypeCategorySE.BASIC,"int"), new ExprListNode(sg.genIntLiteralExpr(env.getMaxVCTSize())));
			VarDeclListNode vd = new VarDeclListNode(new UninitedVarDeclNode(new VarDeclNameNode("vct",null)));
			FieldDeclNode fd = new FieldDeclNode(MemberTypeSE.FIELD_DECLARATION, null, null, at, vd);
			FieldDecl fdecl = new FieldDecl(fd,metaClass,0);
			metaClass.getFields().addField(fdecl);
			fdecl.resolveType(tprov);
		}
	}

}
