/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mopaqlib.MoPaQ;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Finds all trigger source code in a map and returns it as a list.
 * @author us0r
 *
 */
public class TriggerExtractor  
{
	
	
	private static boolean isThere(Element p, String c)
	{
		return p.getElementsByTagName(c).getLength()>0;
	}

	private String actionIndex;
	
	
	public List<Source> extractTriggers(File triggerFile) throws IOException{
		return extractInternal(new InputSource(new FileReader(triggerFile)),triggerFile);
	}
	
	public List<Source> extractTriggers(MoPaQ map) throws IOException{
		byte[] data = map.returnFileByName("triggers");
		ByteArrayInputStream bais=new ByteArrayInputStream(data);
		InputSource is=new InputSource(bais);
		return extractInternal(is, map.getSourceFile());
	}
	
	private List<Source> extractInternal(InputSource is, File source) throws IOException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		ArrayList<Source> out = new ArrayList<Source>();
		
	//	System.out.println(new String(data));
		try {
			db = dbf.newDocumentBuilder();

			Document doc=db.parse(is);
			NodeList allNodes=doc.getElementsByTagName("element");
			//System.out.println(allNodes.getLength());
			for(int i=0;i<allNodes.getLength();i++)
			{
				Element cur=(Element) allNodes.item(i);
				if(cur.getAttribute("type").equalsIgnoreCase("FunctionDef"))
				{
					if(isThere(cur,"flagAction") && isThere(cur,"flagCustomScript") && isThere(cur,"flagHidden"))
					{
						NodeList innerNodes=cur.getElementsByTagName("script");
						for(int j=0;j<innerNodes.getLength();j++)
						{	
//							System.out.println(":::" + cur.getElementsByTagName("identifier").item(0).getTextContent());
							NodeList l = cur.getElementsByTagName("identifier");
							String name;
							if(l.item(0)==null){
								name = "Untitled Action " + actionIndex;
							} else {
								name = l.item(0).getTextContent();
							}
							out.add(new TriggerSource(source,name,innerNodes.item(j).getTextContent()));
//							System.out.println("----------");
//							System.out.println(innerNodes.item(j).getTextContent());
//							System.out.println("----------");
//							sourceCode+=innerNodes.item(j).getTextContent();
//							System.out.println(sourceCode);
//							System.out.println("----------");
						}
					}
				}
			}
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out;
	}
	

}
