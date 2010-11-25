/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.codegen.buffers.AdvancedBuffer;
import com.sc2mod.andromeda.codegen.buffers.FileBuffer;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationType;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.ScopeUtil;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.OutputMemoryStats;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;
import com.sc2mod.andromeda.util.visitors.VoidTreeScanVisitor;

public abstract class CodeGenerator extends VoidTreeScanVisitor {
	
	public FileBuffer fileBuffer;
	public AdvancedBuffer structBuffer;
	public AdvancedBuffer functionBuffer;
	public GlobalVarBuffer globalVarBuffer;
	public AdvancedBuffer typedefBuffer;
	public AdvancedBuffer classInitBuffer;
	
	private int stringLiteralBytes;
	private int bytesOut;
	
	public void addBytesOut(int bytes){
		bytesOut =+ bytes;
	}
	
	public void addStringLiteral(int size){
		stringLiteralBytes += size + 1;
	}

	ClassGenerator classGen;
	protected Configuration options;
	protected Environment env;
	protected INameProvider nameProvider;
	protected Operation errorMethod;
	
	protected boolean insertComments;
	
	public void generateErrorCallPrefix(SimpleBuffer buffer){
		buffer.append(errorMethod.getGeneratedName()).append("(");
	}
	
	private void createBuffers(Configuration c) {
		fileBuffer = new FileBuffer(c);
		structBuffer = new AdvancedBuffer(2048,c);
		functionBuffer = new AdvancedBuffer(8192,c);
		globalVarBuffer = new GlobalVarBuffer(256,c);
		typedefBuffer = new AdvancedBuffer(256,c);
		classInitBuffer = new AdvancedBuffer(512,c);
	}
	
	public CodeGenerator(Environment env2, Configuration options2, INameProvider np) {
		createBuffers(options2);
		this.env = env2;
		this.options = options2;
		this.nameProvider = np;
		this.insertComments = options2.getParamBool(Parameter.CODEGEN_DESCRIPTION_COMMENTS);
		
		if(!env.typeProvider.getClasses().isEmpty()){
			errorMethod = env.typeProvider.getSystemFunction(AndromedaSystemTypes.M_ERROR);
		}
	}



	public CodeGenerator(CodeGenerator codeGenVisitor) {
		this(codeGenVisitor.env, codeGenVisitor.options, codeGenVisitor.nameProvider);
	}

	public void setClassGenerator(ClassGenerator classGen){
		this.classGen = classGen;
		
	}
	public void writeFuncHeader(Function m, SimpleBuffer functionBuffer, String description){
		writeFuncHeader(m, functionBuffer, description,null,null,false);
	}
	

	
	public void writeFuncHeader(Operation m, SimpleBuffer functionBuffer, String description, String alternativeName, String deciderName, boolean isVCT){
		boolean isConstructor = m.getOperationType()==OperationType.CONSTRUCTOR;
		
		if(description!=null&&insertComments){
			functionBuffer.append(description).nl();
		}
		
		//Modifiers
		if(m.isNative()) functionBuffer.append("native ");
		
		//Return type
		if(isConstructor)
			functionBuffer.append(m.getContainingType().getGeneratedName());
		else
			functionBuffer.append(m.getReturnType().getGeneratedName());
		functionBuffer.append(" ");
		
		//Name
		if(alternativeName!=null){
			functionBuffer.append(alternativeName);
		} else {
			functionBuffer.append(m.getGeneratedName());
		}
		
		//Parameters
		functionBuffer.append("(");
		int numParams = m.getSignature().size();
		Variable[] params = m.getParams();
		
		//Decider name?
		if(deciderName!=null){
			functionBuffer.append(deciderName).append(",");
		}
		
		//Method ? Add this
		if(OperationUtil.usesThis(m)){
			classGen.generateThisParam(m);
			functionBuffer.append(m.getContainingType().getGeneratedName());
			functionBuffer.append(" ");
			functionBuffer.append(classGen.getThisName());
			if(numParams > 0)
				functionBuffer.append(",");		
		}
		
		//Constructor? Add implicit cid param
		if(isConstructor){
			if(isVCT){
				functionBuffer.append(",");
				classGen.generateImplicitCidParam(m,1);
			} else {
				classGen.generateImplicitCidParam(m,0);
				
			}
			functionBuffer.append(classGen.getAllocParamType()).append(" ");
			functionBuffer.append(classGen.getCidParam());
			if(numParams > 0)
				functionBuffer.append(",");		
		}
			
		
		for(int i=0;i<numParams;){
			Variable param = params[i];
			functionBuffer.append(param.getType().getGeneratedName());
			functionBuffer.append(" ");
			functionBuffer.append(param.getGeneratedName());
			i++;
			if(i<numParams){
				functionBuffer.append(",");
			}
		}
		functionBuffer.append(")");
	}
	
	public void generateMethodFooter(SimpleBuffer buffer) {
		buffer.unindent().nl().append("}").nl().nl();
	}
	
	public void writeInit(SyntaxNode ast) {
		SimpleBuffer buffer = functionBuffer;
		
		buffer.append("void initAndromeda(){");
		buffer.indent();
		
		buffer.nl();
		buffer.append("trigger t;");
		buffer.nl();
		
		//Only append class init function if there are classes
		if(classGen!=null)
			buffer.append(classGen.getInitFunctionName()).append("();");
		
		//Get sorted static inits
		List<StaticInit> staticInits = new StaticInitCollector().getSortedInits(ast);
	
		
		for(StaticInit s : staticInits) {
			writeStaticInit(s);
		}
		
		generateMethodFooter(buffer);
		functionBuffer.nl();
		
		functionBuffer.flushTo(fileBuffer.functions, true);
		
	}

	private void writeStaticInit(StaticInit s) {
		SimpleBuffer buffer = functionBuffer;
		buffer.nl().append("t = TriggerCreate(\"" + s.getGeneratedName() + "\");");
		buffer.nl().append("TriggerAddEventMapInit(t);");
	}
	

	public OutputMemoryStats getOutputStatistics() {
		return new OutputMemoryStats(globalVarBuffer.getSizeBytes(),stringLiteralBytes,0,bytesOut);
	}
}
