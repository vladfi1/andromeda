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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.SourceLocation;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.Source;

public class ResultXMLWriter {

	
	public void genXML(CompilationResult compilationResult, File xmlFile) throws XMLStreamException, IOException{
		
		XMLWriter writer;
		writer = new XMLWriter(xmlFile);
		
		writer.writeStartDocument();
		writer.writeStartElement("andromedaParseResult");
		writer.writeAttribute("successful", compilationResult.isSuccessful()?"true":"false");
		
			writer.writeStartElement("messages");
			writer.writeAttribute("num",""+compilationResult.getProblems().size());
			writer.writeAttribute("information",String.valueOf(compilationResult.getOthers().size()));
			writer.writeAttribute("warning",String.valueOf(compilationResult.getWarnings().size()));
			writer.writeAttribute("error",String.valueOf(compilationResult.getErrors().size()));
			
			for(Problem m: compilationResult.getProblems()){
				writer.writeStartElement("message");
				writer.writeAttribute("severity", m.getSeverity().name());
				writer.writeAttribute("text", m.getMessage());
				for(SourceLocation s: m.getLocations()){
					writer.writeEmptyElement("location");
					Source a = s.getSource();
					writer.writeAttribute("type", a.getTypeName());
					writer.writeAttribute("name", a.getName());
					writer.writeAttribute("line", String.valueOf(s.getLine()));
					writer.writeAttribute("column", String.valueOf(s.getColumn()));
					writer.writeAttribute("length", String.valueOf(s.getLength()));
					writer.writeAttribute("offset", String.valueOf(s.getFileOffset()));
					writer.writeAttribute("path", a.getFullPath());
				}
				writer.writeEndElement();
			}
		
			writer.writeEndElement();
		writer.writeEndElement();	
		writer.writeEndDocument();
		try {
			writer.close();
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
}
