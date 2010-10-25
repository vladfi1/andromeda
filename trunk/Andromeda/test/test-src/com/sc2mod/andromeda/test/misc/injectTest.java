/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.test.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Platform;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

public class injectTest {

	public static void inject(File from, File to, String nameInArchive) throws IOException, MoPaQException, DataFormatException{
		MoPaQ m = new MoPaQ(to);
		FileInputStream f = new FileInputStream(from);
		byte[] bytes = new byte[(int)from.length()];
		f.read(bytes);
		f.close();
		
		
		m.writeFile(nameInArchive, bytes);
		m.save(to);
	}
	
	public static void main(String[] args) throws IOException, MoPaQException, DataFormatException{
		inject(new File("out/Andromeda.galaxy"),new File("test/sc2TestNoTrigs.SC2Map"),"Andromeda.galaxy");
		System.out.println("FINISHED!");
		MapRunner mr = new MapRunner(new Platform(),"F:\\Spiele\\SC2 beta\\StarCraft II Beta\\StarCraft II.exe","-displaymode 0 -trigdebug -preload 1 -NoUserCheats -reloadcheck -difficulty 0 -speed 2");
		mr.test(new File("test/sc2TestNoTrigs.SC2Map"));
	}
}
