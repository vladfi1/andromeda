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
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.VirtualCallTableGenerator;
import com.sc2mod.andromeda.codegen.CodeGenerator;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;

/**
 * Generates code for virtual call tables.
 * @author J. 'gex' Finis
 *
 */
public class IndexVirtualCallTableGenerator extends VirtualCallTableGenerator{

	private ArrayList<Operation> methodLine = new ArrayList<Operation>(20);
	private SimpleBuffer buffer = new SimpleBuffer(512);
	private SimpleBuffer paramBuffer = new SimpleBuffer(64);
	private SimpleBuffer flushTo;
	private boolean newLine;
	private boolean useIndent;
	private boolean returnsVoid;
	private CodeGenerator generator;
	private boolean insertDescriptionComments;
	private String deciderName;
	private INameProvider nameProvider;
	private IClass metaClass;
	private String virtualCallerName;
	private ClassGenerator classGen;

	
	public IndexVirtualCallTableGenerator(ClassGenerator classGen,IClass metaClass,INameProvider nameProvider, CodeGenerator generator, SimpleBuffer flushTo, Configuration options) {
		super(metaClass,nameProvider,generator,flushTo,options);
		this.flushTo = flushTo;
		this.newLine = options.getParamBool(Parameter.CODEGEN_NEW_LINES);
		this.useIndent = options.getParamBool(Parameter.CODEGEN_USE_INDENT);
		this.generator = generator;
		this.classGen = classGen;
		this.nameProvider = nameProvider;
		this.metaClass = metaClass;
		this.insertDescriptionComments = options.getParamBool(Parameter.CODEGEN_DESCRIPTION_COMMENTS);
	}
	
	public void generateTable(IClass clazz){
		VirtualCallTable vct = clazz.getVirtualCallTable();
		
		generateTable(vct,0);
	}

	private void generateTable(VirtualCallTable vct, int startOffset) {
		ArrayList<Operation> table = vct.table;
		int size = table.size();
		for(int i = startOffset;i<size;i++){
			
			generateMethodLine(vct,i);
		}
		for(VirtualCallTable child: vct.subTables){
			if(child.table.size()>size) generateTable(child,size);
		}
	}

	private int findTwopow(int size) {
		int i = 1;
		while(i<size){
			i<<=1;
		}
		return i;
	}

	private void generateMethodLineInternal(VirtualCallTable vct, int i) {
		Operation m = vct.table.get(i);
		//Only add to the table if not abstract and not yet in the table
		if(m.getOverrideInformation().getVirtualCaller()==null){
			if(!m.isAbstract()) methodLine.add(m);
			m.getOverrideInformation().setVirtualCallerName(virtualCallerName);
		}	
			
		for(VirtualCallTable child: vct.subTables){
			if(child.table.size()>i) generateMethodLineInternal(child, i);
		}
	}
	
	private void generateMethodLine(VirtualCallTable vct, int i){

		virtualCallerName = nameProvider.getGlobalNameRawNoPrefix("vcall___".concat(vct.table.get(i).getGeneratedName()));
		
		generateMethodLineInternal(vct,i);
		
		if(methodLine.isEmpty()) return;
		Operation m = methodLine.get(0);
		returnsVoid = m.getReturnType()==SpecialType.VOID;
		
		//Assemble params
		String comment = null;
		if(insertDescriptionComments){
			comment = "//Virtual caller for ".concat(m.getContainingType().getUid()).concat(".").concat(m.getUid());
		}
		deciderName = nameProvider.getLocalNameRaw("cl", m.getParams().length+1);
		generator.writeFuncHeader(m, buffer, comment
								,virtualCallerName
								,null,true);
		//Forward declaration
		buffer.appendTo(generator.fileBuffer.forwardDeclarations, true);
		generator.fileBuffer.forwardDeclarations.append(";");
		if(newLine) generator.fileBuffer.forwardDeclarations.newLine();
		
		buffer.append("{");
		
		
		int numParams = m.getParams().length;

		
		
		
		if(newLine)buffer.newLine(useIndent?1:0);
		buffer.append(BasicType.INT.getGeneratedName()).append(" ").append(deciderName).append("=");
		buffer.append(metaClass.getNameProvider().getMemoryName()).append("[");
		buffer.append(((IClass)m.getContainingType()).getTopClass().getNameProvider().getMemoryName()).append("[");
		buffer.append(nameProvider.getLocalNameRaw("this", numParams)).append("].");
		buffer.append(((IClass)m.getContainingType()).getHierarchyFields().get(1).getGeneratedName()).append("].");
		buffer.append(ResolveUtil.rawResolveField(metaClass, "vct", null, false).getGeneratedName());
		buffer.append("[").append(m.getOverrideInformation().getVirtualTableIndex()).append("];");
		
		generateSignature(m);
		
		writeMethodLine(0,findTwopow(methodLine.size()),useIndent?1:0);
		
		methodLine.clear();
		
		generator.generateMethodFooter(buffer);
		
		buffer.flushTo(flushTo, true);
		
	}

	private void generateSignature(Operation m) {
		paramBuffer.flush();
		int numParams = m.getParams().length;
		paramBuffer.append(nameProvider.getLocalNameRaw("this", numParams));
		if(numParams > 0)
			paramBuffer.append(",");	

		
		VarDecl[] params = m.getParams();
		for(int i=0;i<numParams;){
			VarDecl param = params[i];
			paramBuffer.append(param.getGeneratedName());
			i++;
			if(i<numParams){
				paramBuffer.append(",");
			}
		}
	}

	private void writeMethodLine(int start, int length, int indent) {
		if(length==1){
			if(newLine)buffer.newLine(indent);
			if(!returnsVoid){
				buffer.append("return ");
			}
			Operation m = methodLine.get(start);
			buffer.append(m.getGeneratedName());
			buffer.append("(");
			paramBuffer.appendTo(buffer, false);
			buffer.append(");");
		} else {
			
			
			length/=2;
			int offset = start + length;		

			
			boolean needIf = offset<methodLine.size();
			if(needIf){
				if(newLine)buffer.newLine(indent);
				buffer.append("if(").append(deciderName).append("<").append(offset).append("){");
			}
			writeMethodLine(start,length,useIndent?(needIf?indent+1:indent):0);		
			
			if(needIf){
				if(newLine)buffer.newLine(indent);
				buffer.append("}");
				buffer.append("else{");
				writeMethodLine(offset,length,useIndent?indent+1:0);
				if(newLine)buffer.newLine(indent);
				buffer.append("}");
			}
		}		
	}

}
