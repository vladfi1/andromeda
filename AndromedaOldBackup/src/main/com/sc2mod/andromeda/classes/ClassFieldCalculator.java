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
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.syntaxNodes.MemberTypeSE;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;

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


	/**
	 * Should be overwritten by subclasses to init implicit class fields (id, type,...)
	 * @param c the class for which to generate the implicit fields
	 * @return a list of implicit fields
	 */
	protected abstract ArrayList<FieldDecl> generateImplicitFields(Class c);
	
	public ClassFieldCalculator(TypeProvider tp, INameProvider snv) {
		this.typeProvider = tp;
		this.nameGenerator = snv;
	}
	
	/**
	 * Creates a field. Can be used by subclasses to create implicit fields conveniently.
	 * @param c For which class the field is
	 * @param type Type of the field
	 * @param name Name of the field (not the generated one, the native one)
	 * @return the generated field
	 */
	protected FieldDecl createField(Class c, Type type,String name){
		VarDeclListNode vd = new VarDeclListNode(new UninitedVarDeclNode(new VarDeclNameNode(name, null)));
		FieldDeclNode fd = new FieldDeclNode(MemberTypeSE.FIELD_DECLARATION, null, null, null, vd );
		FieldDecl f = new FieldDecl(fd, c, 0);
		f.setType(type);
		f.setGeneratedName(nameGenerator.getFieldName(f, c));
		return f;
	}

	

	public void calcFields(){
		for(Class c : typeProvider.getClasses()){
			if(!c.isTopClass()) continue;
			
			ArrayList<FieldDecl> fields = calcHierarchyFields(c, generateImplicitFields(c));
			int size = fields.size();
			for(int i=0;i<size;i++){
				//Enumerate fields, giving them their index
				FieldDecl fd = fields.get(i);
				fd.setFieldIndex(i);
				//And generate their name
				fd.setGeneratedName(nameGenerator.getFieldName(fd, fd.getContainingType()));
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
	private void setFieldsForHierarchy(Class c, ArrayList<FieldDecl> fields){
		c.setHierarchyFields(fields);
		for(RecordType c2: c.getDecendants()){
			setFieldsForHierarchy((Class) c2, fields);
		}
	}

	private ArrayList<FieldDecl> calcHierarchyFields(Class c, ArrayList<FieldDecl> fieldList) {
		//Add all fields from top class without checking (they cannot be reused)
		for(FieldDecl field : c.getFields().getNonStaticClassFields()){
			fieldList.add(field);
		}
		//Call for children in the hierarchy
		int fieldsToSkip = fieldList.size();
		for(RecordType r : c.getDecendants()){
			calcFields((Class)r,fieldList,fieldsToSkip);
		}
		return fieldList;
	}

	private ArrayList<FieldDecl> calcFields(Class t, ArrayList<FieldDecl> fieldList, int startAtIndex) {
		outer: for(FieldDecl field : t.getFields().getNonStaticClassFields()){
			
			//Check if there is a field that could be used
			int size = fieldList.size();

			
			//We start not at zero but skip all fields of the top class
			middle: for(int i=startAtIndex;i<size;i++){
				FieldDecl f = fieldList.get(i);
				if(f.getType().getGeneratedType()== field.getType().getGeneratedType()&&!t.isInstanceof((Class) f.getContainingType())){
					//Field can be reused if it is not yet used in this hierarchy
					ArrayList<FieldDecl> usedBy = f.getUsedByFields();
					if(usedBy != null){
						for(FieldDecl f2: usedBy){
							//Field is already used in this hierarchy, skip!
							if(t.isInstanceof((Class)f2.getContainingType())) continue middle;
						}
					}
					
					//Field can be reused
					((FieldDecl)field).setUsesField(f);
					continue outer;
				}
			}
			
			//No field to use could be found, add to list
			fieldList.add(field);
			
		}
		
		//Call for children in the hierarchy
		for(RecordType r : t.getDecendants()){
			calcFields((Class)r,fieldList,startAtIndex);
		}
		return fieldList;
	}
	
	
	public abstract void generateClassNames();
}
