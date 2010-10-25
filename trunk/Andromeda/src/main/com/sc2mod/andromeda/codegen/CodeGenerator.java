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
import com.sc2mod.andromeda.codegen.buffers.FileBuffer;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.ScopeUtil;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.OutputMemoryStats;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

public abstract class CodeGenerator extends VoidVisitorAdapter {
	
	public FileBuffer fileBuffer = new FileBuffer();
	public SimpleBuffer structBuffer = new SimpleBuffer(2048);
	public SimpleBuffer functionBuffer = new SimpleBuffer(8192);
	public GlobalVarBuffer globalVarBuffer = new GlobalVarBuffer(256);
	public SimpleBuffer typedefBuffer = new SimpleBuffer(256);
	public SimpleBuffer classInitBuffer = new SimpleBuffer(512);
	
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
	protected boolean newLines;
	protected boolean whitespaceInExprs;
	protected boolean useIndent;
	protected boolean ownLineForOpenBraces;
	
	public void generateErrorCallPrefix(SimpleBuffer buffer){
		buffer.append(errorMethod.getGeneratedName()).append("(");
	}
	
	public CodeGenerator(Environment env2, Configuration options2, INameProvider np) {
		this.env = env2;
		this.options = options2;
		this.nameProvider = np;
		this.insertComments = options2.getParamBool(Parameter.CODEGEN_DESCRIPTION_COMMENTS);
		this.newLines = options2.getParamBool(Parameter.CODEGEN_NEW_LINES);
		this.useIndent = options2.getParamBool(Parameter.CODEGEN_USE_INDENT);
		this.whitespaceInExprs = options2.getParamBool(Parameter.CODEGEN_WHITESPACES_IN_EXPRS);
		this.ownLineForOpenBraces = options2.getParamBool(Parameter.CODEGEN_OWN_LINE_FOR_OPEN_BRACES);
		
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
		boolean isConstructor = m.getFunctionType()==Function.TYPE_CONSTRUCTOR;
		
		if(description!=null&&insertComments){
			functionBuffer.append(description);
			functionBuffer.newLine();
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
		VarDecl[] params = m.getParams();
		
		//Decider name?
		if(deciderName!=null){
			functionBuffer.append(deciderName).append(",");
		}
		
		//Method ? Add this
		if(m.usesThis()){
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
			VarDecl param = params[i];
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
		if(newLines){
			buffer.newLine();
			buffer.append("}");
			buffer.newLine();
			buffer.newLine();
		} else {
			buffer.append("}");
	
		}
	}
	
	public void writeInit(SyntaxNode ast) {
		SimpleBuffer buffer = functionBuffer;
		boolean newLines = this.newLines;
		
		buffer.append("void initAndromeda(){");
		int indent = 0;
		if(useIndent)indent++;

		if(newLines)buffer.newLine(indent);	
		buffer.append("trigger t;");
		if(newLines)buffer.newLine(indent);	
		
		//Only append class init function if there are classes
		if(classGen!=null)
			buffer.append(classGen.getInitFunctionName()).append("();");
		
		//Get sorted static inits
		List<StaticInit> staticInits = new StaticInitSorter().getSortedInits(ast);
	
		
		for(StaticInit s : staticInits) {
			writeStaticInit(s, indent);
		}
		
		generateMethodFooter(buffer);
		if (newLines)
			functionBuffer.newLine();
		
		functionBuffer.flushTo(fileBuffer.functions, true);
		
	}

	private void writeStaticInit(StaticInit s, int indent) {
		SimpleBuffer buffer = functionBuffer;
		if(newLines)buffer.newLine(indent);
		buffer.append("t = TriggerCreate(\"" + s.getGeneratedName() + "\");");
		if(newLines)buffer.newLine(indent);
		buffer.append("TriggerAddEventMapInit(t);");
	}
	

	public OutputMemoryStats getOutputStatistics() {
		return new OutputMemoryStats(globalVarBuffer.getSizeBytes(),stringLiteralBytes,0,bytesOut);
	}
}
