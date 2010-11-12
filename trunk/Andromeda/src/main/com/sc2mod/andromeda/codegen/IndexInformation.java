package com.sc2mod.andromeda.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.util.Pair;

public class IndexInformation {

	//********* CLASS INDICES *****
	private HashMap<IClass, Pair<Integer, Integer>> classIndices = new HashMap<IClass, Pair<Integer,Integer>>();
	
	public void addClassInformation(ClassImpl cls, int classIndex, int instanceofIndex){
		classIndices.put(cls, new Pair<Integer, Integer>(classIndex, instanceofIndex));
	}
	
	public int getClassIndex(IClass c){
		return classIndices.get(c)._1;
	}
	
	public int getClassMinInstanceofIndex(IClass c){
		return classIndices.get(c)._2;
	}
	
	//********* FIELD INDICES *******
	private HashMap<Variable, Integer> fieldIndices = new HashMap<Variable, Integer>();
	
	public void setFieldIndex(Variable fd, int index){
		fieldIndices.put(fd, index);
	}
	
	public int getFieldIndex(FieldDecl fd){
		return fieldIndices.get(fd);
	}
	
	private HashMap<Variable,ArrayList<Variable>> aliasInformation = new HashMap<Variable,ArrayList<Variable>>();
	
	public void setFieldAliasing(Variable usedField, Variable usingField){
		if(aliasInformation.containsKey(usedField)){
			aliasInformation.get(usedField).add(usingField);
		} else {
			ArrayList<Variable> list = new ArrayList<Variable>(4);
			list.add(usingField);
			aliasInformation.put(usedField, list);
		}
	}
	
	public ArrayList<Variable> getFieldAliases(Variable f){
		return aliasInformation.get(f);
	}
	
	public boolean isFieldAliased(FieldDecl usedField){
		return aliasInformation.containsKey(usedField);
	}
	
	
}
