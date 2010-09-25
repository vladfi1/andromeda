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
import com.sc2mod.andromeda.syntaxNodes.ArrayType;
import com.sc2mod.andromeda.syntaxNodes.BasicType;
import com.sc2mod.andromeda.syntaxNodes.ClassMemberType;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeCategory;
import com.sc2mod.andromeda.syntaxNodes.VariableDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;

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
			ArrayType at = new ArrayType(TypeCategory.ARRAY, new BasicType(TypeCategory.BASIC,"int"), new ExpressionList(sg.genIntLiteralExpr(env.getMaxVCTSize())));
			VariableDeclarators vd = new VariableDeclarators(new VariableDecl(new VariableDeclaratorId("vct",null)));
			FieldDeclaration fd = new FieldDeclaration(ClassMemberType.FIELD_DECLARATION, null, null, at, vd);
			FieldDecl fdecl = new FieldDecl(fd,metaClass,0);
			metaClass.getFields().addField(fdecl);
			fdecl.resolveType(tprov);
		}
	}

}
