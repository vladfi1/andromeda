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

import java.util.List;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.codegen.buffers.FileBuffer;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.OutputStats;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;

public abstract class CodeGenerator extends VisitorAdaptor {
	
	public FileBuffer fileBuffer = new FileBuffer();
	public SimpleBuffer structBuffer = new SimpleBuffer(2048);
	public SimpleBuffer functionBuffer = new SimpleBuffer(8192);
	public GlobalVarBuffer globalVarBuffer = new GlobalVarBuffer(256);
	public SimpleBuffer typedefBuffer = new SimpleBuffer(256);
	public SimpleBuffer classInitBuffer = new SimpleBuffer(512);
	
	private int stringLiteralBytes;
	
	public void addStringLiteral(int size){
		stringLiteralBytes += size + 1;
	}

	ClassGenerator classGen;
	protected Options options;
	protected Environment env;
	protected INameProvider nameProvider;
	protected Class systemClass;
	protected AbstractFunction errorMethod;
	
	public void generateErrorCallPrefix(SimpleBuffer buffer){
		buffer.append(errorMethod.getGeneratedName()).append("(");
	}
	
	public CodeGenerator(Environment env2, Options options2, INameProvider np) {
		this.env = env2;
		this.options = options2;
		this.nameProvider = np;
		systemClass = env.typeProvider.getClass("System");
		if(systemClass == null){
			throw new CompilationError("Internal class \"System\" is missing!");
		}
		
		Signature s = new Signature(new Type[]{BasicType.STRING});
		
		errorMethod = systemClass.getMethods().getMethod("error",s);
		if(errorMethod == null){
			throw new CompilationError("The internal class \"System\" is missing the error(string) method!");
		}
	}

	public void setClassGenerator(ClassGenerator classGen){
		this.classGen = classGen;
		
	}
	public void writeFuncHeader(Function m, SimpleBuffer functionBuffer, String description){
		writeFuncHeader(m, functionBuffer, description,null,null,false);
	}
	

	
	public void writeFuncHeader(AbstractFunction m, SimpleBuffer functionBuffer, String description, String alternativeName, String deciderName, boolean isVCT){
		boolean isConstructor = m.getFunctionType()==Function.TYPE_CONSTRUCTOR;
		
		if(description!=null&&options.insertDescriptionComments){
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
		if(options.newLines){
			buffer.newLine();
			buffer.append("}");
			buffer.newLine();
			buffer.newLine();
		} else {
			buffer.append("}");
	
		}
	}
	
	public void writeInit(){
		SimpleBuffer buffer = functionBuffer;
		boolean newLines = options.newLines;
		
		buffer.append("void initAndromeda(){");
		int indent = 0;
		if(options.useIndent)indent++;

		if(newLines)buffer.newLine(indent);	
		buffer.append("trigger t;");
		if(newLines)buffer.newLine(indent);	
		buffer.append(classGen.getInitFunctionName()).append("();");
		
		for(StaticInit s : env.getGlobalInits()){
			writeStaticInit(s,indent);
		}
		for(StaticInit s: env.typeProvider.getTypeInits()){
			writeStaticInit(s,indent);
		}
		
		generateMethodFooter(buffer);
		if (options.newLines)
			functionBuffer.newLine();
		
		functionBuffer.flushTo(fileBuffer.functions, true);
		
	}

	private void writeStaticInit(StaticInit s, int indent) {
		SimpleBuffer buffer = functionBuffer;
		if(options.newLines)buffer.newLine(indent);
		buffer.append("t = TriggerCreate(\"" + s.getGeneratedName() + "\");");
		if(options.newLines)buffer.newLine(indent);
		buffer.append("TriggerAddEventMapInit(t);");
	}
	

	public OutputStats getOutputStatistics() {
		return new OutputStats(globalVarBuffer.getSizeBytes(),stringLiteralBytes,0);
	}
}
