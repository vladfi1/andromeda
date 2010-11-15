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

import java.util.ArrayList;

import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.IndexInformation;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.impl.RecordTypeImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

/**
 * Calculates which field a class hierarchy needs. Also adds implicit fields
 * 
 * This is needed at the moment, since a class hierarchy is compiled to
 * only ONE struct which should have as few fields as possible to save space.
 * 
 * Should get obsolete if we find a way to cast easily between structs so each
 * class can become its own struct.
 * 
 * @author J. 'gex' Finis
 *
 */
public abstract class ClassFieldCalculator {

	protected TypeProvider typeProvider;
	protected INameProvider nameGenerator;
	private IndexInformation indexInfo;


	/**
	 * Should be overwritten by subclasses to init implicit class fields (id, type,...)
	 * @param c the class for which to generate the implicit fields
	 * @return a list of implicit fields
	 */
	protected abstract ArrayList<Variable> generateImplicitFields(IClass c);
	
	public ClassFieldCalculator(TypeProvider tp, INameProvider snv, IndexInformation indexInfo) {
		this.typeProvider = tp;
		this.nameGenerator = snv;
		this.indexInfo = indexInfo;
	}
	
	/**
	 * Creates a field. Can be used by subclasses to create implicit fields conveniently.
	 * @param c For which class the field is
	 * @param type Type of the field
	 * @param name Name of the field (not the generated one, the native one)
	 * @return the generated field
	 */
	protected FieldDecl createField(IClass c, IType type,String name){
		VarDeclNode vdn = new UninitedVarDeclNode(new IdentifierNode(name));
		VarDeclListNode vd = new VarDeclListNode();
		FieldDeclNode fd = new FieldDeclNode(null, null, null, vd , null);
		FieldDecl f = new FieldDecl(fd, vdn, c, c);
		f.setResolvedType(type);
		f.setGeneratedName(nameGenerator.getFieldName(f, c));
		return f;
	}

	

	public void calcFields(){
		for(IClass c : typeProvider.getClasses()){
			if(!c.isTopType()) continue;
			
			ArrayList<Variable> fields = calcHierarchyFields(c, generateImplicitFields(c));
			int size = fields.size();
			for(int i=0;i<size;i++){
				//Enumerate fields, giving them their index
				Variable fd = fields.get(i);
				indexInfo.setFieldIndex(fd, i);
				
				//And generate their name
				fd.setGeneratedName(nameGenerator.getFieldName((FieldDecl) fd, fd.getContainingType()));
			}
			setFieldsForHierarchy(c,fields);
			//c.setHierarchyFields(fields);

			
			
		}
	}

	/**
	 * This method is called after the fields for a class hierarchy are calculated and
	 * sets the calculated fields as hierarchy fields for each class in the hierarchy
	 * @param c
	 * @param fields
	 */
	private void setFieldsForHierarchy(IClass c, ArrayList<Variable> fields){
		c.setHierarchyFields(fields);
		for(IDeclaredType c2: c.getDescendants()){
			setFieldsForHierarchy((IClass) c2, fields);
		}
	}

	private ArrayList<Variable> calcHierarchyFields(IClass c, ArrayList<Variable> fieldList) {
		//Add all fields from top class without checking (they cannot be reused)
		for(Variable field : TypeUtil.getNonStaticTypeFields(c, false)){
			fieldList.add(field);
		}
		//Call for children in the hierarchy
		int fieldsToSkip = fieldList.size();
		for(IDeclaredType r : c.getDescendants()){
			calcFields((IClass)r,fieldList,fieldsToSkip);
		}
		return fieldList;
	}

	private ArrayList<Variable> calcFields(IClass t, ArrayList<Variable> fieldList, int startAtIndex) {
		outer: for(Variable field : TypeUtil.getNonStaticTypeFields(t, false)){
			
			//Check if there is a field that could be used
			int size = fieldList.size();

			
			//We start not at zero but skip all fields of the top class
			middle: for(int i=startAtIndex;i<size;i++){
				Variable f = fieldList.get(i);
				if(f.getType().getGeneratedType()== field.getType().getGeneratedType()&&!t.isSubtypeOf(f.getContainingType())){
					//Field can be reused if it is not yet used in this hierarchy
					ArrayList<Variable> usedBy = indexInfo.getFieldAliases(f);
					if(usedBy != null){
						for(Variable f2: usedBy){
							//Field is already used in this hierarchy, skip!
							if(t.isSubtypeOf(f2.getContainingType())) continue middle;
						}
					}
					
					//Field can be reused
					indexInfo.setFieldAliasing(field, f);
					continue outer;
				}
			}
			
			//No field to use could be found, add to list
			fieldList.add(field);
			
		}
		
		//Call for children in the hierarchy
		for(IDeclaredType r : t.getDescendants()){
			calcFields((IClass)r,fieldList,startAtIndex);
		}
		return fieldList;
	}
	
	
	public abstract void generateClassNames();
}
