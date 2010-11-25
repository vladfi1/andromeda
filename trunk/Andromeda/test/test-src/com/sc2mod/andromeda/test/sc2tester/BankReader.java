package com.sc2mod.andromeda.test.sc2tester;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sc2mod.andromeda.problems.InternalProgramError;

/**
 * Reader for sc2 bank files
 * @author gex
 *
 */
public class BankReader {

	private File bank;

	private Document buildDOM(File bank) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(bank);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			throw new InternalProgramError("Couldn't read bank file ",e);
		}
	}

	public BankReader(File bank) {
		this.bank = bank;
	}
	
	public Document read(){
		if(!bank.exists()){
			return null;
		}
		
		return buildDOM(bank);
	}
}
