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
import com.sc2mod.andromeda.codegen.IndexInformation;
import com.sc2mod.andromeda.codegen.buffers.AdvancedBuffer;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;

/**
 * Generates class code (allocators, constructors, deallocators, class init, class structs,...)
 * @author J. 'gex' Finis
 */
public class IndexClassGenerator extends ClassGenerator {

	private static final String TYPE_BIT_OFFSET = "24";
	private Operation metaClassConstructor;
	private String classInitLocal;
	private String allocParamTypeName;
	private String classInitFunctionName;
	private boolean insertDescriptionComments;
	private IndexInformation indexInformation;
	

	public IndexClassGenerator(IndexInformation indexInformation, Environment env, CodeGenVisitor c,INameProvider nameProvider,Configuration options) {
		super(env,c,nameProvider,options);
		this.indexInformation = indexInformation;
		insertDescriptionComments = options.getParamBool(Parameter.CODEGEN_DESCRIPTION_COMMENTS);
		allocParamTypeName = metaClass.getGeneratedName();
		metaClassConstructor = env.typeProvider.getSystemFunction(AndromedaSystemTypes.CONS_CLASS);
		
		
	}
	
	public void generateClasses(IClass metaClass, ArrayList<IClass> arrayList){
		generateClassInitHeader(arrayList);
		
		//remove metaClass from list (it has to be processed first)
		arrayList.remove(metaClass);
		
		generateClass(metaClass);
		
		for(IClass r: arrayList){
			// non top classes are left out because they re visited recursively
			if(r.isTopType()){
				generateClass(r);
			}
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
	


	private void generateClassInitHeader(ArrayList<IClass> arrayList) {
		classInitLocal = nameProvider.getLocalNameRaw("A__class", 0);
		SimpleBuffer buffer = codeGenVisitor.classInitBuffer;
		classInitFunctionName = nameProvider.getGlobalNameRaw("classInit");
		buffer.append(BASIC.VOID.getGeneratedName()).append(" ").append(classInitFunctionName).append("(){");
		buffer.indent().nl();
		buffer.append(metaClass.getGeneratedName()).append(" ").append(classInitLocal).append(";");

		
	}

	private void generateClass(IClass c){
		
		
		if(!c.isStaticElement()){
			if(c.isTopType()){

				
				//Generate Class Struct
				generateClassStruct(c);
				
				//Generate allocator
				generateAllocation(c);
				
				//Generate vct
				VirtualCallTableGenerator vctg = new IndexVirtualCallTableGenerator(BASIC,this,metaClass,nameProvider,codeGenVisitor,codeGenVisitor.fileBuffer.functions, options);
				vctg.generateTable(c);
				generationCount++;
				
			} else {
				c.getNameProvider().setMemoryName(c.getTopClass().getNameProvider().getMemoryName());
				
				if(TypeUtil.hasTypeFieldInits(c))
					generateFieldInit(c);
			}
			
			//generate child classes
			for(IDeclaredType c2 : c.getDescendants()){
				generateClass((IClass) c2);
			}
		}
		
		//Init meta class and virtual call table (if the class has one)
		generateClassInit(c);
	}
	
	private void generateClassInit(IClass c){
		SimpleBuffer buffer = codeGenVisitor.classInitBuffer;
		Configuration options = this.options;
		
		//Implicit params
		String classLocal = classInitLocal;
		
		//Comment
//		if(options.insertDescriptionComments){
//			buffer.newLine(codeGenVisitor.curIndent);
//			buffer.append("//Class: " + c.getName());
//		}
		
		//Create class: A__class = new metaclass(id, name);
		buffer.nl();
		buffer.append(classLocal).append("=").append(metaClassConstructor.getGeneratedName());
		buffer.append("(").append(indexInformation.getClassIndex(c)).append(",").append(indexInformation.getClassIndex(c)).append(",").appendStringLiteral(c.getUid(), codeGenVisitor).append(");");
		
		String name = nameProvider.getGlobalNameRawNoPrefix("MC___" + c.getNameProvider().getStructName());
		c.setMetaClassName(name);
		
		codeGenVisitor.globalVarBuffer.beginVarDecl(metaClass, name).append(";");
		codeGenVisitor.globalVarBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		codeGenVisitor.globalVarBuffer.nl();
		
		buffer.nl();
		buffer.append(name).append("=").append(classLocal).append(";");
		
		
		//Virtual call table
		ArrayList<Operation> vct = c.getVirtualCallTable().getTable();
		String memoryName = metaClass.getNameProvider().getMemoryName();
		if(vct.size()>0){
			String vctName = ResolveUtil.rawResolveField(metaClass,"vct", null, false).getGeneratedName();
			int size = vct.size();
			for(int i=0;i<size;i++){
				buffer.nl();
				Operation m = vct.get(i);
				generateFieldAccess(buffer,memoryName,classLocal).append(vctName).append("[").append(i).append("]");
				if(m.getModifiers().isAbstract()){
					buffer.append("=").append(-1).append(";");
				} else {
					buffer.append("=").append(m.getOverrideInformation().getVirtualCallIndex()).append(";");
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
	private void generateClassStruct(IClass c){
		ArrayList<Variable> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.structBuffer;
		
		
		buffer.append("struct ");
		buffer.append(c.getNameProvider().getStructName());
		buffer.append("{");
		buffer.indent();
		for(Variable f: fields){
			buffer.nl();
			buffer.append(f.getType().getGeneratedName());
			buffer.append(" ");
			buffer.append(f.getGeneratedName());
			buffer.append(";");
		}
		buffer.unindent();
		buffer.nl();
		buffer.append("};");
		buffer.nl().nl();
		buffer.flushTo(codeGenVisitor.fileBuffer.types,true);
	}
	
	private String nameAllocPtr;
	private String nameFreePtr;
	private String nameMemory;
	private String nameFreeStack;
	
	private void generateAllocation(IClass c){
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
	
	private void generateAllocator(IClass c){
		ArrayList<Variable> fields = c.getHierarchyFields();
		AdvancedBuffer buffer = codeGenVisitor.functionBuffer;
		Configuration options = this.options;
		String className = c.getGeneratedName();
		String typeFieldName = fields.get(1).getGeneratedName();
		String idFieldName = fields.get(0).getGeneratedName();

		//Implicit params
		curClassIdName = nameProvider.getLocalNameRaw("A__class", 0);
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		
		//Comment
		if(this.insertDescriptionComments){
			buffer.append("//Allocator for class " + c.getName()).nl();
		}
		
		buffer.append(className).append(" ").append(c.getNameProvider().getAllocatorName()).append("(");
		buffer.append(allocParamTypeName).append(" ").append(curClassIdName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";").nl();
		
		buffer.append("{");
		buffer.indent().nl();
		buffer.append(className).append(" ").append(curThisName).append(";");
		generateAllocCode(c,buffer);

		buffer.nl();
		generateFieldAccess(buffer,nameMemory,curThisName).append(typeFieldName).append("=").append(curClassIdName).append(";");
		buffer.nl();
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("=(").append(curClassIdName).append("<<").append(TYPE_BIT_OFFSET).append(")|this;");
		
		for(Variable f: TypeUtil.getNonStaticTypeFields(c, false)){
			if(f.isInitedInDecl()){
				buffer.nl();
				codeGenVisitor.generateFieldInit(buffer,c,f);
			}
		}
		buffer.nl();
		buffer.append("return ").append(curThisName).append(";");
		buffer.unindent().nl();
		buffer.append("}");
		buffer.nl().nl();
		
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}


	private void generateAllocCode(IClass c, SimpleBuffer buffer) {
		int maxAlloc = (c.getInstanceLimit()+1);
		String className = c.getGeneratedDefinitionName();
		IType typeInt = BASIC.INT;
		
		//Create local variables
		GlobalVarBuffer globalsBuffer = codeGenVisitor.globalVarBuffer;
		globalsBuffer.beginVarDecl(typeInt, nameAllocPtr).append("=1;");
		globalsBuffer.nl();
		globalsBuffer.beginVarDecl(typeInt,nameFreePtr).append(";");
		globalsBuffer.nl();
		globalsBuffer.beginArrayDecl(c,c.getNameProvider().getStructName(),maxAlloc,nameMemory).append(";");
		globalsBuffer.nl();
		globalsBuffer.beginArrayDecl(c,maxAlloc,nameFreeStack).append(";");
		globalsBuffer.nl();
		globalsBuffer.flushTo(codeGenVisitor.fileBuffer.variables, true);
		
		//Create alloc function code
		buffer.nl();
		buffer.append("if(").append(nameFreePtr).append(">0){");
		buffer.indent().nl();
		
		//Free stack case
		buffer.append(nameFreePtr).append("-=1;").nl();
		buffer.append(curThisName).append("=").append(nameFreeStack).append("[").append(nameFreePtr).append("];");
		buffer.unindent().nl();
		
		buffer.append("}else if(").append(nameAllocPtr).append("<").append(maxAlloc).append("){");
		buffer.indent().nl();
		
		//Alloc new case
		buffer.append(curThisName).append("=").append(nameAllocPtr).append(";");
		buffer.nl();
		buffer.append(nameAllocPtr).append("+=1;");
		buffer.unindent().nl();
		buffer.append("}else{");
		buffer.indent().nl();
		
		//Error case
		codeGenVisitor.generateErrorCallPrefix(buffer);
		String s = "Instance limit reached for class " + c.getName() + ". Allocation failed!";
		buffer.appendStringLiteral(s, codeGenVisitor).append(");");
	
		buffer.nl();
		buffer.append("return 0;");
		buffer.unindent().nl().append("}");

	
	}
	
	private void generateDeallocator(IClass c){
		ArrayList<Variable> fields = c.getHierarchyFields();
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		
		String idFieldName = fields.get(0).getGeneratedName();
		String typeFieldName = fields.get(1).getGeneratedName();
		
		//Comment
		if(insertDescriptionComments){
			buffer.append("//Deallocator for class " + c.getName());
			buffer.nl();
		}
		
		buffer.append("void ").append(c.getNameProvider().getDeallocatorName()).append("(");
		buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";").nl();
		
		buffer.append("{");
		buffer.indent().nl();
		buffer.append("if(");
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("==0){");
		buffer.indent().nl();
		codeGenVisitor.generateErrorCallPrefix(buffer);
		String s = "Double free of class "+ c.getName();
		buffer.appendStringLiteral(s, codeGenVisitor);
		buffer.append(");");
		buffer.nl();
		buffer.append("return;");
		buffer.unindent().nl();
		buffer.append("}");
		buffer.nl();
		generateFieldAccess(buffer,nameMemory,curThisName).append(idFieldName).append("=0;");
		buffer.nl();
		generateFieldAccess(buffer,nameMemory,curThisName).append(typeFieldName).append("=0;");
		buffer.nl();
		buffer.append(nameFreeStack).append("[").append(nameFreePtr).append("]=").append(curThisName).append(";");
		buffer.nl();
		buffer.append(nameFreePtr).append("+=1;");
		buffer.unindent().nl();
		buffer.append("}").nl().nl();
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}
	

	private void generateFieldInit(IClass c){
		Iterable<Variable> fields = TypeUtil.getNonStaticTypeFields(c, false);
		AdvancedBuffer buffer = codeGenVisitor.functionBuffer;
		curThisName = nameProvider.getLocalNameRaw("this", 1);
		
		//Comment
		if(insertDescriptionComments){
			buffer.append("//Field init for class " + c.getName());
			buffer.nl();
		}
		
		buffer.append(c.getGeneratedName()).append(" ").append(c.getNameProvider().getAllocatorName()).append("(");
		buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append(")");
		
		//Forward decl
		buffer.appendTo(codeGenVisitor.fileBuffer.forwardDeclarations, true);
		codeGenVisitor.fileBuffer.forwardDeclarations.append(";").nl();
		
		buffer.append("{");
		buffer.indent();
		for(Variable f: fields){
			if(f.isInitedInDecl()){
				buffer.nl();
				codeGenVisitor.generateFieldInit(buffer,c,f);
			}
		}
		buffer.nl();
		buffer.append("return ").append(curThisName).append(";");
		buffer.unindent().nl();
		buffer.append("}").nl().nl();
		
		buffer.flushTo(codeGenVisitor.fileBuffer.functions, true);
	}
	

	public void generateConstructorHead(Constructor m) {
		SimpleBuffer buffer = codeGenVisitor.functionBuffer;
		IClass c = (IClass) m.getContainingType();
		ConstructorInvocation i = m.getInvokedConstructor();

		//No construct or invoked? This must be a top class, so call the allocator
		if(i.isImplicit()){
			buffer.nl();
			buffer.append(c.getGeneratedName()).append(" ").append(curThisName).append("=");
			generateConstructorInvocation(i, null,null);
			buffer.append(";");
		}
		
	}
	
	public void generateConstructorInvocation(ConstructorInvocation inv, ExprListNode arguments,IClass forClass){
		SimpleBuffer buffer = codeGenVisitor.curBuffer;
		
		
		//Wrap field inits around
		ArrayList<IClass> fieldInits = inv.getWrappedFieldInits();
		int size = fieldInits==null?0:fieldInits.size();
		for(int i=size-1;i>=0;i--){
			buffer.append(fieldInits.get(i).getNameProvider().getAllocatorName()).append("(");		
		}
		
		Operation f = inv.getWhichFunction();
		if(f == null){
			//We have an allocator call
			IClass c = inv.getClassToAlloc();
			buffer.append(c.getNameProvider().getAllocatorName());
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
	public void generateFieldAccessPrefix(SimpleBuffer curExprBuffer, IClass c) {
		curExprBuffer.append(c.getNameProvider().getMemoryName()).append("[");
		
	}

	@Override
	public void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, IClass c) {
		curExprBuffer.append("].");
	}

	@Override
	public String getInitFunctionName() {
		return classInitFunctionName;
	}


}
