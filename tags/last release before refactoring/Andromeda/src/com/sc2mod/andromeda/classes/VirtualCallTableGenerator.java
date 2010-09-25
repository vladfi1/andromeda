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

import com.sc2mod.andromeda.codegen.CodeGenerator;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.program.Options;

/**
 * Generates code for virtual call tables.
 * @author J. 'gex' Finis
 *
 */
public abstract class VirtualCallTableGenerator {

	private ArrayList<AbstractFunction> methodLine = new ArrayList<AbstractFunction>(20);
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
	private Class metaClass;
	private String virtualCallerName;

	
	public VirtualCallTableGenerator(Class metaClass,INameProvider nameProvider, CodeGenerator generator, SimpleBuffer flushTo, Options options) {
		super();
		this.flushTo = flushTo;
		this.newLine = options.newLines;
		this.useIndent = options.useIndent;
		this.generator = generator;
		this.nameProvider = nameProvider;
		this.metaClass = metaClass;
		this.insertDescriptionComments = options.insertDescriptionComments;
	}
	
	public void generateTable(Class clazz){
		VirtualCallTable vct = clazz.getVirtualCallTable();
		
		generateTable(vct,0);
	}

	private void generateTable(VirtualCallTable vct, int startOffset) {
		ArrayList<AbstractFunction> table = vct.table;
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
		AbstractFunction m = vct.table.get(i);
		m.setVirtualCallerName(virtualCallerName);
		if(!m.isAbstract()) methodLine.add(m);
		
		for(VirtualCallTable child: vct.subTables){
			if(child.table.size()>i) generateMethodLineInternal(child, i);
		}
	}
	
	private void generateMethodLine(VirtualCallTable vct, int i){

		virtualCallerName = nameProvider.getGlobalNameRaw("vcall_".concat(vct.table.get(i).getGeneratedName()));
		
		generateMethodLineInternal(vct,i);
		
		if(methodLine.isEmpty()) return;
		AbstractFunction m = methodLine.get(0);
		returnsVoid = m.getReturnType()==SpecialType.VOID;
		
		//Assemble params
		String comment = null;
		if(insertDescriptionComments){
			comment = "//Virtual caller for ".concat(m.getContainingType().getUid()).concat(".").concat(m.getUid());
		}
		deciderName = nameProvider.getLocalNameRaw("cl", m.getParams().length+1);
		generator.writeFuncHeader(m, buffer, comment
								,virtualCallerName
								,null,false);
		//Forward declaration
		buffer.appendTo(generator.fileBuffer.forwardDeclarations, true);
		generator.fileBuffer.forwardDeclarations.append(";");
		if(newLine) generator.fileBuffer.forwardDeclarations.newLine();
		
		buffer.append("{");
		if(newLine)buffer.newLine(useIndent?1:0);
		buffer.append(BasicType.INT.getGeneratedName()).append(" ").append(deciderName).append("=");
		buffer.append(nameProvider.getLocalNameRaw("this", m.getParams().length)).append("->");
		buffer.append(((Class)m.getContainingType()).getHierarchyFields().get(1).getGeneratedName()).append("->");
		buffer.append(metaClass.getFields().getFieldByName("vct").getGeneratedName());
		buffer.append("[").append(m.getVirtualTableIndex()).append("];");
		
		generateSignature(m);
		
		writeMethodLine(0,findTwopow(methodLine.size()),useIndent?1:0);
		
		methodLine.clear();
		
		generator.generateMethodFooter(buffer);
		
		buffer.flushTo(flushTo, true);
		
	}

	private void generateSignature(AbstractFunction m) {
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
			AbstractFunction m = methodLine.get(start);
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
