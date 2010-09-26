/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes.indexSys;

import java.util.ArrayList;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.VirtualCallTableGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;

/**
 * Generates class code (allocators, constructors, deallocators, class init, class structs,...)
 * @author J. 'gex' Finis
 */
public class IndexClassGenerator extends ClassGenerator {

	private static final String TYPE_BIT_OFFSET = "24";
	private AbstractFunction metaClassConstructor;
	private String classInitLocal;
	private String allocParamTypeName;
	private String classInitFunctionName;
	

	public IndexClassGenerator(Environment env, CodeGenVisitor c,INameProvider nameProvider,Configuration options) {
		super(env,c,nameProvider,options);
	
		allocParamTypeName = metaClass.getGeneratedName();
		metaClassConstructor = env.typeProvider.getSystemFunction(AndromedaSystemTypes.CONS_CLASS);
		
		
	}
	
	public void generateClasses(ArrayList<Class> arrayList){
		generateClassInitHeader(arrayList);
		
		for(Class r: arrayList){
			generateClass(r);
		}
		
		codeGenVisitor.generateMethodFooter(codeGenVisitor.classInitBuffer);
		
		//If classes were generated we need a global id count
		/*if(generationCount>0){
			if(options.insertDescriptionComments){
				codeGenVisitor.globalVarBuffer.append("//Global class id count");
				codeGenVisitor.globalVarBuffer.newLine();
			}
			codeGenVisitor.globalVarBuffer.append(BasicType.INT.getGeneratedName()).append(" ").append(globalClassIdName).append("=1;");
			//if(options.newLines)codeGenVisitor.globalVarBuffer.newLine();
			codeGenVisitor.globalVarBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		}*/
	}
	


	private void generateClassInitHeader(ArrayList<Class> arrayList) {
		classInitLocal = nameProvider.getLocalNameRaw("A__class", 0);
		SimpleBuffer buffer = codeGenVisitor.classInitBuffer;
		classInitFunctionName = nameProvider.getGlobalNameRaw("classInit");
		buffer.append(SpecialType.VOID.getGeneratedName()).append(" ").append(classInitFunctionName).append("(){");
		if(options.newLines)
			buffer.newLine(1);
		buffer.append(metaClass.getGeneratedName()).append(" ").append(classInitLocal).append(";");

		
	}

	private void generateClass(Class c){
		VirtualCallTableGenerator vctg = new IndexVirtualCallTableGenerator(this,metaClass,nameProvider,codeGenVisitor,codeGenVisitor.fileBuffer.functions, options);

		
		if(!c.isStatic()){
			if(c.isTopClass()){

				
				//Generate Class Struct
				generateClassStruct(c);
				
				//Generate allocator
				generateAllocation(c);
				
				//Generate vct
				vctg.generateTable(c);
				
				generationCount++;
			} else {
				c.getNameProvider().setMemoryName(c.getTopClass().getNameProvider().getMemoryName());
				
				if(c.hasFieldInit())
					generateFieldInit(c);
			}
		}
		
		//Init meta class and virtual call table (if the class has one)
		generateClassInit(c);
	}
	
	private void generateClassInit(Class c){
		SimpleBuffer buffer = codeGenVisitor.classInitBuffer;
		Configuration options = this.options;
		boolean newLines = options.newLines;
		int indent = 1;
		
		//Implicit params
		String classLocal = classInitLocal;
		
		//Comment
//		if(options.insertDescriptionComments){
//			buffer.newLine(codeGenVisitor.curIndent);
//			buffer.append("//Class: " + c.getName());
//		}
		
		//Create class: A__class = new metaclass(id, name);
		if(newLines)buffer.newLine(indent);
		buffer.append(classLocal).append("=").append(metaClassConstructor.getGeneratedName());
		buffer.append("(").append(c.getClassIndex()).append(",").append(c.getClassIndex()).append(",").appendStringLiteral(c.getUid(), codeGenVisitor).append(");");
		
		String name = nameProvider.getGlobalNameRawNoPrefix("MC___" + c.getNameProvider().getStructName());
		c.setMetaClassName(name);
		
		codeGenVisitor.globalVarBuffer.beginVarDecl(metaClass, name).append(";");
		codeGenVisitor.globalVarBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		if(newLines)codeGenVisitor.globalVarBuffer.newLine();
		
		if(newLines)buffer.newLine(indent);
		buffer.append(name).append("=").append(classLocal).append(";");
		
		
		//Virtual call table
		ArrayList<AbstractFunction> vct = c.getVirtualCallTable().getTable();
		String memoryName = metaClass.getNameProvider().getMemoryName();
		if(vct.size()>0){
			String vctName = metaClass.getFields().getFieldByName("vct").getGeneratedName();
			int size = vct.size();
			for(int i=0;i<size;i++){
				if(newLines)buffer.newLine(indent);
				AbstractFunction m = vct.get(i);
				generateFieldAccess(buffer,memoryName,classLocal).append(vctName).append("[").append(i).append("]");
				if(m.isAbstract()){
					buffer.append("=").append(-1).append(";");
				} else {
					buffer.append("=").append(m.getVirtualCallIndex()).append(";");
				}
			}
		}
		
	}
	
	private SimpleBuffer generateFieldAccess(SimpleBuffer buffer, String memoryName, String thisName){
		buffer.append(memoryName).append("[").append(thisName).append("].");
		return buffer;
	}
	
	/**
	 * Creates the struct that will hold the class instances
	 * @param c
	 */
	private void generateClassStruct(Class c){
		ArrayList<FieldDecl> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.structBuffer;
		int indent = codeGenVisitor.curIndent;
		Configuration options = this.options;
		
		buffer.append("struct ");
		buffer.append(c.getNameProvider().getStructName());
		buffer.append("{");
		if(options.useIndent) indent++;		
		for(FieldDecl f: fields){
			if(options.newLines)buffer.newLine(indent);	
			buffer.append(f.getType().getGeneratedName());
			buffer.append(" ");
			buffer.append(f.getGeneratedName());
			buffer.append(";");
		}
		if(options.useIndent) indent--;
		if(options.newLines)buffer.newLine(indent);	
		buffer.append("};");
		if(options.newLines){
			buffer.newLine(indent);	
			buffer.newLine(indent);	
		}
		buffer.flushTo(codeGenVisitor.fileBuffer.types,true);
	}
	
	private String nameAllocPtr;
	private String nameFreePtr;
	private String nameMemory;
	private String nameFreeStack;
	
	private void generateAllocation(Class c){
		String className = c.getNameProvider().getStructName();
		
		
		//Global variable names
		nameAllocPtr = nameProvider.getGlobalNameRawNoPrefix(className.concat("___allocPtr"));
		nameFreePtr = nameProvider.getGlobalNameRawNoPrefix(className.concat("___freePtr"));
		nameMemory = nameProvider.getGlobalNameRawNoPrefix(className.concat("___memory"));
		nameFreeStack = nameProvider.getGlobalNameRawNoPrefix(className.concat("___freeStack"));
		
		c.getNameProvider().setMemoryName(nameMemory);
		
		generateAllocator(c);
		generateDeallocator(c);
	}
	
	private void generateAllocator(Class c){
		ArrayList<FieldDecl> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		Configuration options = this.options;
		String className = c.getGeneratedName();
		String typeFieldName = fields.get(1).getGeneratedName();
		String idFieldName = fields.get(0).getGeneratedName();

		//Implicit params
		curClassIdName = nameProvider.getLocalNameRaw("A__class", 0);
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		
		//Comment
		if(options.insertDescriptionComments){
			buffer.append("//Allocator for class " + c.getName());
			buffer.newLine(codeGenVisitor.curIndent);
		}
		
		buffer.append(className).append(" ").append(c.getAllocatorName()).append("(");
		buffer.append(allocParamTypeName).append(" ").append(curClassIdName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";");
		if(options.newLines) codeGenVisitor.fileBuffer.forwardDeclarations.newLine();
		
		buffer.append("{");
		if(options.useIndent) codeGenVisitor.curIndent++;
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		buffer.append(className).append(" ").append(curThisName).append(";");
		generateAllocCode(c,buffer);

		
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		
		generateFieldAccess(buffer,nameMemory,curThisName).append(typeFieldName).append("=").append(curClassIdName).append(";");
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("=(").append(curClassIdName).append("<<").append(TYPE_BIT_OFFSET).append(")|this;");
		
		for(FieldDecl f: c.getFields().getNonStaticClassFields()){
			if(f.isInitDecl()){
				if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
				codeGenVisitor.generateFieldInit(buffer,c,f);
			}
		}
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		buffer.append("return ").append(curThisName).append(";");
		if(options.useIndent) codeGenVisitor.curIndent--;
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		buffer.append("}");
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		
		
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}


	private void generateAllocCode(Class c, SimpleBuffer buffer) {
		boolean newLines = options.newLines;
		boolean useIndent = options.useIndent;
		int maxAlloc = (c.getInstanceLimit()+1);
		int indent = codeGenVisitor.curIndent;
		String className = c.getGeneratedDefinitionName();
		Type typeInt = BasicType.INT;
		

		
		//Create local variables
		GlobalVarBuffer globalsBuffer = codeGenVisitor.globalVarBuffer;
		globalsBuffer.beginVarDecl(typeInt, nameAllocPtr).append("=1;");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.beginVarDecl(typeInt,nameFreePtr).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.beginArrayDecl(c,c.getNameProvider().getStructName(),maxAlloc,nameMemory).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.beginArrayDecl(c,maxAlloc,nameFreeStack).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		
		//Create alloc function code
		if(newLines)buffer.newLine(indent);
		buffer.append("if(").append(nameFreePtr).append(">0){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		//Free stack case
		buffer.append(nameFreePtr).append("-=1;");
		if(newLines)buffer.newLine(indent);
		buffer.append(curThisName).append("=").append(nameFreeStack).append("[").append(nameFreePtr).append("];");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}else if(").append(nameAllocPtr).append("<").append(maxAlloc).append("){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		//Alloc new case
		buffer.append(curThisName).append("=").append(nameAllocPtr).append(";");
		if(newLines)buffer.newLine(indent);
		buffer.append(nameAllocPtr).append("+=1;");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}else{");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		//Error case
		codeGenVisitor.generateErrorCallPrefix(buffer);
		String s = "Instance limit reached for class " + c.getName() + ". Allocation failed!";
		buffer.appendStringLiteral(s, codeGenVisitor).append(");");
	
		if(newLines)buffer.newLine(indent);
		buffer.append("return 0;");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}");

	
	}
	
	private void generateDeallocator(Class c){
		ArrayList<FieldDecl> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		boolean newLines = options.newLines;
		boolean useIndent = options.useIndent;
		int indent = codeGenVisitor.curIndent;
		String idFieldName = fields.get(0).getGeneratedName();
		String typeFieldName = fields.get(1).getGeneratedName();
		
		//Comment
		if(options.insertDescriptionComments){
			buffer.append("//Deallocator for class " + c.getName());
			buffer.newLine(codeGenVisitor.curIndent);
		}
		
		buffer.append("void ").append(c.getDeallocatorName()).append("(");
		buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";");
		if(newLines) codeGenVisitor.fileBuffer.forwardDeclarations.newLine();
		
		buffer.append("{");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		buffer.append("if(");
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("==0){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		codeGenVisitor.generateErrorCallPrefix(buffer);
		String s = "Double free of class "+ c.getName();
		buffer.appendStringLiteral(s, codeGenVisitor);
		buffer.append(");");
		if(newLines)buffer.newLine(indent);
		buffer.append("return;");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}");
		if(newLines)buffer.newLine(indent);
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("=0;");
		if(newLines)buffer.newLine(indent);
		generateFieldAccess(buffer,nameMemory,curThisName).append(typeFieldName).append("=0;");
		if(newLines)buffer.newLine(indent);
		buffer.append(nameFreeStack).append("[").append(nameFreePtr).append("]=").append(curThisName).append(";");
		if(newLines)buffer.newLine(indent);
		buffer.append(nameFreePtr).append("+=1;");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}");
		if(newLines){
			buffer.newLine(indent);
			buffer.newLine(indent);
		}
		
		
		
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}
	

	private void generateFieldInit(Class c){
		ArrayList<FieldDecl> fields = c.getFields().getNonStaticClassFields();
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		boolean newLines = options.newLines;
		boolean useIndent = options.useIndent;
		int indent = codeGenVisitor.curIndent;
		
		//Comment
		if(options.insertDescriptionComments){
			buffer.append("//Field init for class " + c.getName());
			buffer.newLine(indent);
		}
		
		buffer.append(c.getGeneratedName()).append(" ").append(c.getAllocatorName()).append("(");
		buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";");
		if(newLines) codeGenVisitor.fileBuffer.forwardDeclarations.newLine();
		
		buffer.append("{");
		if(newLines){
			if(useIndent)indent++;
		}
		for(FieldDecl f: c.getFields().getNonStaticClassFields()){
			if(f.isInitDecl()){
				if(options.newLines) buffer.newLine(indent);
				codeGenVisitor.generateFieldInit(buffer,c,f);
			}
		}
		if(newLines) buffer.newLine(indent);
		buffer.append("return ").append(curThisName).append(";");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}");
		if(newLines) buffer.newLine(indent);
		if(newLines) buffer.newLine(indent);
		
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}
	

	public void generateConstructorHead(Constructor m) {
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		Class c = (Class) m.getContainingType();
		ConstructorInvocation i = m.getInvokedConstructor();

		//No construct or invoked? This must be a top class, so call the allocator
		if(i.isImplicit()){
			if(options.newLines)buffer.newLine(codeGenVisitor.curIndent);
			buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append("=");
			generateConstructorInvocation(i, null,null);
			buffer.append(";");
		}
		
	}
	
	public void generateConstructorInvocation(ConstructorInvocation inv, ExprListNode arguments,Class forClass){
		SimpleBuffer buffer = codeGenVisitor.curBuffer;
		
		
		//Wrap field inits around
		ArrayList<Class> fieldInits = inv.getWrappedFieldInits();
		int size = fieldInits==null?0:fieldInits.size();
		for(int i=size-1;i>=0;i--){
			buffer.append(fieldInits.get(i).getAllocatorName()).append("(");		
		}
		
		AbstractFunction f = inv.getWhichFunction();
		if(f == null){
			//We have an allocator call
			Class c = inv.getClassToAlloc();
			buffer.append(c.getAllocatorName());
		} else {
			//We have a constructor call
			buffer.append(inv.getWhichFunction().getGeneratedName());
		}
		buffer.append("(");
		if(forClass==null){
			buffer.append(curClassIdName);
		} else {
			buffer.append(forClass.getMetaClassName());
		}
		if(arguments!=null&&!arguments.isEmpty()){
			buffer.append(",");
			Signature sigBefore = codeGenVisitor.expressionVisitor.getDesiredSignature();
			codeGenVisitor.expressionVisitor.setDesiredSignature(inv.getWhichFunction().getSignature());
			codeGenVisitor.invokeRValueVisitor(arguments,true);
			codeGenVisitor.expressionVisitor.setDesiredSignature(sigBefore);
		}		
		buffer.append(")");
		
		//Wrap field init ends around
		for(int i=size-1;i>=0;i--){
			buffer.append(")");		
		}
		
	}

	public void generateImplicitCidParam(Function m) {		
		//Generate the implicit parameter
		int index = m.getLocals().length+m.getParams().length;
		curClassIdName = nameProvider.getLocalNameRaw("A__class", index);
		
	}

	public void generateThisParam(Function m, boolean isConstructor) {
		int index = m.getLocals().length+m.getParams().length + (isConstructor?1:0);
		curThisName = nameProvider.getLocalNameRaw("this", index);
	}

	public String getAllocParamType() {
		return allocParamTypeName;
	}

	@Override
	public void generateFieldAccessPrefix(SimpleBuffer curExprBuffer, Class c) {
		curExprBuffer.append(c.getNameProvider().getMemoryName()).append("[");
		
	}

	@Override
	public void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, Class c) {
		curExprBuffer.append("].");
	}

	@Override
	public String getInitFunctionName() {
		return classInitFunctionName;
	}


}
