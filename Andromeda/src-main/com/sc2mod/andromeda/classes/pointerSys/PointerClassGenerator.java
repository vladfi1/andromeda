/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes.pointerSys;

import java.util.ArrayList;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.VirtualCallTableGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;

/**
 * Generates class code (allocators, constructors, deallocators, class init, class structs,...)
 * @author J. 'gex' Finis
 */
public class PointerClassGenerator extends ClassGenerator {

	private String globalClassIdName;
	private Constructor metaClassConstructor;
	private String classInitLocal;
	private String allocParamTypeName;
	

	public PointerClassGenerator(Environment env, CodeGenVisitor c,INameProvider nameProvider,Configuration options) {
		super(env,c,nameProvider,options);
	
		allocParamTypeName = metaClass.getGeneratedName();
		metaClassConstructor = metaClass.resolveConstructorCall(metaClass.getDeclaration(), new Signature(new Type[]{BasicType.INT,BasicType.STRING}), Visibility.PRIVATE);
		
		if(null==metaClassConstructor){
			//throw new CompilationError(metaClass.getDeclaration(),"Meta class is missing constructor (int,string).");
		}
		
		globalClassIdName = nameProvider.getGlobalNameRaw("A__class__id");
	}
	
	public void generateClasses(ArrayList<Class> arrayList){
		generateClassInitHeader();
		
		for(Class r: arrayList){
			generateClass(r);
		}
		
		codeGenVisitor.generateMethodFooter(codeGenVisitor.classInitBuffer);
		
		//If classes were generated we need a global id count
		if(generationCount>0){
			if(options.insertDescriptionComments){
				codeGenVisitor.globalVarBuffer.append("//Global class id count");
				codeGenVisitor.globalVarBuffer.newLine();
			}
			codeGenVisitor.globalVarBuffer.append(BasicType.INT.getGeneratedName()).append(" ").append(globalClassIdName).append("=1;");
			//if(options.newLines)codeGenVisitor.globalVarBuffer.newLine();
			codeGenVisitor.globalVarBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		}
	}
	


	private void generateClassInitHeader() {
		classInitLocal = nameProvider.getLocalNameRaw("A__class", 0);
		SimpleBuffer buffer = codeGenVisitor.classInitBuffer;
		String name = nameProvider.getGlobalNameRaw("classInit");
		buffer.append(SpecialType.VOID.getGeneratedName()).append(" ").append(name).append("(){");
		if(options.newLines)
			buffer.newLine(1);
		buffer.append(metaClass.getGeneratedName()).append(" ").append(classInitLocal).append(";");
	}

	private void generateClass(Class c){
		VirtualCallTableGenerator vctg = null;//new IndexVirtualCallTableGenerator(metaClass,nameProvider,codeGenVisitor,codeGenVisitor.fileBuffer.functions, options);
		
		//Init meta class and virtual call table (if the class has one)
		generateClassInit(c);
		
		if(!c.isStatic()){
			if(c.isTopClass()){
				//Generate vct
				vctg.generateTable(c);
				
				//Generate Class Struct
				generateClassStruct(c);
				
				//Generate allocator
				generateAllocation(c);
				
				generationCount++;
			} else {
				if(c.hasFieldInit())
					generateFieldInit(c);
			}
		}
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
		buffer.append("(").append(c.getClassIndex()).append(",\"").append(c.getUid()).append("\");");
		
		String name = nameProvider.getGlobalNameRaw("MC_" + c.getGeneratedDefinitionName());
		c.setMetaClassName(name);
		
		codeGenVisitor.globalVarBuffer.append(metaClass.getGeneratedName()).append(" ").append(name).append(";");
		codeGenVisitor.globalVarBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		if(newLines)codeGenVisitor.globalVarBuffer.newLine();
		
		if(newLines)buffer.newLine(indent);
		buffer.append(name).append("=").append(classLocal);
		
		
		//Virtual call table
		ArrayList<AbstractFunction> vct = c.getVirtualCallTable().getTable();
		if(vct.size()>0){
			String vctName = metaClass.getFields().getFieldByName("vct").getGeneratedName();
			int size = vct.size();
			for(int i=0;i<size;i++){
				if(newLines)buffer.newLine(indent);
				AbstractFunction m = vct.get(i);
				buffer.append(classLocal).append("->").append(vctName).append("[").append(i).append("]");
				if(m.isAbstract()){
					buffer.append("=").append(-1).append(";");
				} else {
					buffer.append("=").append(m.getVirtualCallIndex()).append(";");
				}
			}
		}
		
	}
	
	private void generateClassStruct(Class c){
		ArrayList<FieldDecl> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.structBuffer;
		int indent = codeGenVisitor.curIndent;
		Configuration options = this.options;
		
		buffer.append("struct ");
		buffer.append(c.getGeneratedDefinitionName());
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
		buffer.append("}");
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
		String className = c.getGeneratedDefinitionName();
		//Global variable names
		nameAllocPtr = nameProvider.getGlobalNameRaw(className.concat("__allocPtr"));
		nameFreePtr = nameProvider.getGlobalNameRaw(className.concat("__freePtr"));
		nameMemory = nameProvider.getGlobalNameRaw(className.concat("__memory"));
		nameFreeStack = nameProvider.getGlobalNameRaw(className.concat("__freeStack"));
		
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
		buffer.append(curThisName).append("->").append(typeFieldName).append("=").append(curClassIdName).append(";");
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		buffer.append(globalClassIdName).append("+=1;");
		if(options.newLines) buffer.newLine(codeGenVisitor.curIndent);
		buffer.append(curThisName).append("->").append(idFieldName).append("=").append(globalClassIdName).append(";");
		
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
		String maxAlloc = String.valueOf(c.getInstanceLimit());
		int indent = codeGenVisitor.curIndent;
		String className = c.getGeneratedDefinitionName();
		String intName = BasicType.INT.getGeneratedName();
		

		
		//Create local variables
		SimpleBuffer globalsBuffer = codeGenVisitor.globalVarBuffer;
		globalsBuffer.append(intName).append(" ").append(nameAllocPtr).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.append(intName).append(" ").append(nameFreePtr).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.append(className).append("[").append(maxAlloc).append("]").append(" ").append(nameMemory).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.append(className).append("*[").append(maxAlloc).append("]").append(" ").append(nameFreeStack).append(";");
		if(newLines)globalsBuffer.newLine();
		globalsBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		
		//Create alloc function code
		if(newLines)buffer.newLine(indent);
		buffer.append("if(").append(nameFreePtr).append(">0){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		buffer.append(nameFreePtr).append("-=1;");
		if(newLines)buffer.newLine(indent);
		buffer.append(curThisName).append("=").append(nameFreeStack).append("[").append(nameFreePtr).append("];");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}else if(").append(nameAllocPtr).append("<=").append(maxAlloc).append("){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		buffer.append(curThisName).append("=&").append(nameMemory).append("[").append(nameAllocPtr).append("];");
		if(newLines)buffer.newLine(indent);
		buffer.append(nameAllocPtr).append("+=1;");
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
		
		//Comment
		if(options.insertDescriptionComments){
			buffer.append("//Deallocator for class " + c.getName());
			buffer.newLine(codeGenVisitor.curIndent);
		}
		
		buffer.append(c.getGeneratedName()).append(" ").append(c.getDeallocatorName()).append("(");
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
		buffer.append("if(").append(curThisName).append("->").append(idFieldName).append("==0){");
		if(newLines){
			if(useIndent)indent++;
			buffer.newLine(indent);
		}
		buffer.append("return;");
		if(newLines){
			if(useIndent)indent--;
			buffer.newLine(indent);
		}
		buffer.append("}");
		if(newLines)buffer.newLine(indent);
		buffer.append(curThisName).append("->").append(idFieldName).append("=0;");
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
	
	public void generateConstructorInvocation(ConstructorInvocation inv, ExpressionList arguments,Class forClass){
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
			codeGenVisitor.invokeRValueVisitor(arguments,true);
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
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, Class c) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getInitFunctionName() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}


}
