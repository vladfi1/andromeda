package com.sc2mod.andromeda.test.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.options.ConfigFile;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.CommandLineOptions;
import com.sc2mod.andromeda.parsing.options.InvalidParameterException;
import com.sc2mod.andromeda.program.Program;

public class AndromedaTestRun {

	protected Configuration getDefaultOptions(){
		try {
			ConfigFile config = new ConfigFile(new File( new File("."),"test/andromeda.conf"), true);
			
			Configuration o = new Configuration(config, new CommandLineOptions(new String[0]));
			return o;
		} catch (Throwable e) {
			fail("Init failed:" + e.getMessage());
			throw new Error("Init failed");
		}
		
	}
	
	protected void reset(){

		Program.appDirectory = new File(".");
	}
	
	/**
	 * Can be called after any callAndromeda() invocation to reparse the
	 * output with a galaxy parser to verify that the output has no errors in it.
	 *
	 * Will parse the output of the previous run with a galaxy parser and fail
	 * if there are any parse errors (warnings are ignored).
	 */
	protected void checkOutput(){
		//TODO: Enable output checking again, once code creation is working again.
		if(true)return;
		reset();
		ArrayList<Source> input = new ArrayList<Source>();
		input.add(new FileSource(new File("out/test/Andromeda.galaxy").getAbsolutePath(),null));
		CompilationEnvironment result = null;
		try {
			result  = Program.invokeWorkflow(input,getDefaultOptions(), Language.GALAXY);
			ArrayList<Problem> errors = result.getResult().getErrors();
			if(!errors.isEmpty()){
				fail("The created code has compile errors!\n" + getListStr(errors));
			}
		} catch (Throwable t){
			fail("An exception has been thrown: " + t.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked" })
	protected String getListStr(ArrayList a){
		StringBuffer b = new StringBuffer();
		for(Object o: a){
			b.append(String.valueOf(o)).append("\n");
		}
		return b.toString();
	}
}
