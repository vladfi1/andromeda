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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLWriter {

	private XMLStreamWriter writer;

	public XMLWriter(File xmlFile) throws XMLStreamException, IOException {
		xmlFile.getAbsoluteFile().getParentFile().mkdirs();
		XMLOutputFactory factory = XMLOutputFactory.newInstance(); 
		writer = factory.createXMLStreamWriter(  
		                                   new XMLFormatWriter(new BufferedWriter(new FileWriter(xmlFile))) ); 
		
	}
	
	public void writeStartDocument() {
		try {			
			writer.writeStartDocument();
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
	
	public void writeEndDocument() {
		try {
			writer.writeEndDocument();
			
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
//	
//	private void newLine() {
//		if(formatMode == FORMAT_NONE) return;
//		try {
//			out.write("\r\n");
//			if(formatMode == FORMAT_INDENT){
//				out.write(indentStrs[curIndent]);
//			}
//		} catch (IOException e) {
//			throw new XMLError(e);
//		}
//		
//	}

	public void writeStartElement(String localName){
		try {
			writer.writeStartElement(localName);
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
	
	public void writeEndElement(){
		try {
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
	
	public void writeAttribute(String localName, String value){
		try {
			writer.writeAttribute(localName, value);
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}

	public void close() throws XMLStreamException {
		writer.close();
	}

	public void writeEmptyElement(String localName) {
		try {
			writer.writeEmptyElement(localName);
		} catch (XMLStreamException e) {
			throw new XMLError(e);
		}
	}
	
	
	
}
