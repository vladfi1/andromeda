package com.sc2mod.andromeda.test.sc2tester;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
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
	
	public BankContent read(){
		if(!bank.exists()){
			return null;
		}
		
		Document d = buildDOM(bank);
		
		return buildBankContent(d);
	}

	private BankContent buildBankContent(Document d) {
		BankContent content = new BankContent();
		
		NodeList sections = d.getElementsByTagName("Section");
		for(int i=0;i<sections.getLength();i++){
			buildSection(content,sections.item(i));
		}
		
		return content;
	}

	private void buildSection(BankContent content, Node item) {
		BankSection section = new BankSection();
		String name = item.getAttributes().getNamedItem("name").getTextContent();
		item.normalize();
		content.put(name, section);
		NodeList keys = item.getChildNodes();
		for(int i=0;i<keys.getLength();i++){
			buildKey(section,keys.item(i));
		}
		
	}

	private void buildKey(BankSection section, Node item) {
		if(item.getNodeType() != Node.ELEMENT_NODE)
			return;
		
		String key = item.getAttributes().getNamedItem("name").getTextContent();
		if(!item.getNodeName().equals("Key"))
			throw new InternalProgramError("Section contains an element that is no Key!");
		NodeList values = item.getChildNodes();
		for(int i=0;i<values.getLength();i++){
			buildValue(section,key,values.item(i));
		}
		
	}

	private void buildValue(BankSection section, String key, Node item) {
		if(item.getNodeType() != Node.ELEMENT_NODE)
			return;
		if(!item.getNodeName().equals("Value"))
			throw new InternalProgramError("Key has a non-value subelement");
		Node val = item.getAttributes().getNamedItem("string");
		String stringVal = val.getTextContent();
		section.put(key, stringVal);
	}

}
