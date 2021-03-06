/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.xml.gen;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.NonParamDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceInfo;
import com.sc2mod.andromeda.parsing.SourceManager;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

public class StructureXMLVisitor extends VoidVisitorAdapter{

	
	private XMLWriter writer;
	private SourceManager env;
	private boolean inRecord;
	private Configuration options;
	
	public StructureXMLVisitor(Configuration options){
		this.options = options;
	}
	
	public void genXml(SourceManager env, File xmlFile, SourceListNode code) throws XMLStreamException, IOException{
		writer = new XMLWriter(xmlFile);
		this.env = env;
		inRecord = false;
		
		writer.writeStartDocument();
		writer.writeStartElement("andromedaStructure");
		code.childrenAccept(this);
		writer.writeEndElement();
		writer.writeEndDocument();
	
		writer.close();
	}
	
	private void writePosition(SyntaxNode syntaxNode) {
		int offset = syntaxNode.getLeftPos()&0x00FFFFFF;
		int length = (syntaxNode.getRightPos()&0x00FFFFFF)-offset;
		writer.writeAttribute("offset", String.valueOf(offset));
		writer.writeAttribute("length", String.valueOf(length));
		
	}
	
	@Override
	public void visit(SourceFileNode andromedaFile) {
		XMLWriter writer = this.writer;
		SourceInfo info = andromedaFile.getSourceInfo();
		InclusionType inclusionType = info.getType();
		
		//Omit natives if not desired to output them
		if(inclusionType==InclusionType.NATIVE&&!options.getParamBool(Parameter.XML_OUTPUT_NATIVES)){
			return;
		}
		
		writer.writeStartElement("source");
		
		Source source = env.getSourceById(info.getFileId());
		writer.writeAttribute("name", source.getName());
		writer.writeAttribute("type", source.getTypeName());
		writer.writeAttribute("inclusionType", String.valueOf(inclusionType));
		writer.writeAttribute("path", source.getFullPath());
				
		andromedaFile.getContent().childrenAccept(this);
		writer.writeEndElement();
	}
	
	@Override
	public void visit(IncludeNode includedFile) {
		includedFile.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalVarDeclNode globalVarDeclaration) {
		globalVarDeclaration.childrenAccept(this);
	}
	
	
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		XMLWriter writer = this.writer;
		writer.writeStartElement("class");
		

		
		IClass c = (IClass)classDeclaration.getSemantics();
		writer.writeAttribute("name", c.getName());
		writePosition(classDeclaration);
		writer.writeAttribute("visibility", c.getVisibility().getName());
		if(c.getModifiers().isStatic()){
			writer.writeAttribute("static", "true");
		}
		if(c.getModifiers().isFinal()){
			writer.writeAttribute("final", "true");
		}
		if(c.getSuperClass()!=null){
			writer.writeAttribute("extends", c.getName());
		}
		
		boolean inRecordBefore = inRecord;
		inRecord=true;		
		classDeclaration.getBody().childrenAccept(this);
		inRecord=inRecordBefore;
		
		writer.writeEndElement();
	}
	
	@Override
	public void visit(StructDeclNode structDeclaration) {
		XMLWriter writer = this.writer;
		writer.writeStartElement("struct");
		writer.writeAttribute("name", structDeclaration.getName());
		writePosition(structDeclaration);
		
		boolean inRecordBefore = inRecord;
		inRecord=true;		
		structDeclaration.childrenAccept(this);
		inRecord=inRecordBefore;
		
		writer.writeEndElement();
	}
	


	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		XMLWriter writer = this.writer;
		writer.writeStartElement("enrich");
		
		Enrichment c = (Enrichment)enrichDeclaration.getSemantics();
		writer.writeAttribute("type", c.getEnrichedType().getUid());
		writePosition(enrichDeclaration);
		
		boolean inRecordBefore = inRecord;
		inRecord=true;		
		enrichDeclaration.getBody().childrenAccept(this);
		inRecord=inRecordBefore;
		
		writer.writeEndElement();
	}
	
	@Override
	public void visit(MethodDeclNode methodDeclaration) {
		XMLWriter writer = this.writer;
		Function f = (Function) methodDeclaration.getSemantics();
		if(OperationUtil.isForwardDeclaration(f)) return;
		if(inRecord){
			writer.writeStartElement("method");
		} else {
			writer.writeStartElement("function");
			
		}
		
		
		writer.writeAttribute("name", f.getName());
		writePosition(methodDeclaration);
		writer.writeAttribute("visibility", f.getVisibility().getName());
		
		
		if(inRecord){
			if(f.getModifiers().isFinal()){
				writer.writeAttribute("final", "true");
			}
			if(f.getModifiers().isAbstract()){
				writer.writeAttribute("abstract", "true");
			}
			if(f.getModifiers().isStatic()){
				writer.writeAttribute("static", "true");
			}
				
		} else {
			if(f.getModifiers().isNative()){
				writer.writeAttribute("native", "true");
			}
				
		}
		
		if(f.isInline()){
			writer.writeAttribute("inline", "true");
		}
		if(f.getModifiers().isOverride()){
			writer.writeAttribute("override", "true");
		}
		writer.writeAttribute("signature", f.getSignature().toString());
		writer.writeAttribute("returnType", f.getReturnType().getUid());
		
		ParamDecl[] params = f.getParams();
		for(ParamDecl p: params){
			writer.writeEmptyElement("param");
			writer.writeAttribute("name", p.getUid());
			writer.writeAttribute("type", p.getType().getUid());

		}
		LocalVarDecl[] locals = f.getLocals();
		for(LocalVarDecl l: locals){
			writer.writeEmptyElement("local");
			writer.writeAttribute("name", l.getUid());
			writer.writeAttribute("type", l.getType().getUid());

		}
		
		writer.writeEndElement();
	}
	
	@Override
	public void visit(FieldDeclNode fieldDeclaration) {
		XMLWriter writer = this.writer;
		int size = fieldDeclaration.getDeclaredVariables().size();
		for(int i = 0; i < size; i++){
			SyntaxNode sn =  fieldDeclaration.getDeclaredVariables().get(i);
			NonParamDecl fd = (NonParamDecl) sn.getSemantics();
			
			if(inRecord){
				writer.writeEmptyElement("field");
			} else {
				writer.writeEmptyElement("globalVariable");
			}
			
			writer.writeAttribute("name", fd.getUid());
			writePosition(sn);
			writer.writeAttribute("type", fd.getType().getUid());
			writer.writeAttribute("visibility", fd.getVisibility().getName());
			
			if(fd.getModifiers().isConst()){
				writer.writeAttribute("const","true");
				writer.writeAttribute("value", fd.getValue().toString());
			}
			if(fd.getModifiers().isStatic()){
				writer.writeAttribute("static", "true");
			}
		}
	}

	
}
