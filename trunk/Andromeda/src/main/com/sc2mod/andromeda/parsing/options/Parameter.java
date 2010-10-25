package com.sc2mod.andromeda.parsing.options;

import static com.sc2mod.andromeda.parsing.options.ParamType.*;

import java.io.File;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.ProblemResponse;
import com.sc2mod.andromeda.util.Files;

public enum Parameter implements ICLOption, IConfigFileEntry{
	
	//Debug output
	DEBUG_PRINT_ERROR_STACK_TRACE(FLAG,false,"DEBUG","printErrorStackTrace","--printErrorStackTrace"),

	//Run config
	RUN_CONFIG(STRING,"debug","GENERAL","defaultRunConfig","-r"),
	RUN_CONFIG_LIST(COMMA_LIST,"debug","GENERAL","runConfigs"),
	
	//Codegen
	CODEGEN_SHORTEN_VAR_NAMES(FLAG,false,".CODE_GEN","shortenVarNames"),
	CODEGEN_USE_INDENT(FLAG,true,".CODE_GEN","useIndent"),
	CODEGEN_NEW_LINES(FLAG,true,".CODE_GEN","newLines"),
	CODEGEN_WHITESPACES_IN_EXPRS(FLAG,true,".CODE_GEN","whitespacesInExprs"),
	CODEGEN_OWN_LINE_FOR_OPEN_BRACES(FLAG,false,".CODE_GEN","ownLineForOpenBraces"),
	CODEGEN_DESCRIPTION_COMMENTS(FLAG,false,".CODE_GEN","insertDescriptionComments"),
	
	//Problems
	PROBLEM_UNREACHABLE_CODE(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","unreachableCode"),
	PROBLEM_UNUSED_GLOBAL(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","unusedGlobals"),
	PROBLEM_UNUSED_FIELD(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","unusedFields"),
	PROBLEM_UNUSED_STATIC_FIELD(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","unusedFields"),
	PROBLEM_UNUSED_LOCAL(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","unusedLocals"),
	PROBLEM_UNUSED_METHODS(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","uncalledMethods"),
	PROBLEM_UNUSED_FUNCTIONS(PROBLEM_RESPONSE,ProblemResponse.ERROR,".PROBLEMS","uncalledFunctions"),
	
	//Optimization
	OPTIMIZE_RESOLVE_CONSTANT_EXPRS(FLAG,true,".OPTIMIZATION","resolveConstantExpressions"),
	OPTIMIZE_INLINE_STRING_CONSTS_CHAR_COUNT(INT,20,".OPTIMIZATION","inlineStringConstsUpToXChars"),
	
	//Testing
	TEST_SC2_PARAMS(STRING,"",".TEST","sc2params"),
	TEST_RUN_MAP_AFTER_COMPILE(FLAG,false,"--run"),
	TEST_CHECK_NATIVE_FUNCTION_BODIES(FLAG,false,".TEST","checkNativeFunctionBodies"),
	
	//Files
	FILES_NATIVE_LIB_FOLDER(APP_FILE,Files.getAppFile("./nativeLib"),"GENERAL","nativeLibFolder"){
		@Override public void doAdditionalChecks(Object value) throws InvalidParameterException 
		{ Files.checkForDir((File)value, true);}
	},
	FILES_LIB_FOLDERS(APP_FILES,new File[]{Files.getAppFile("./lib")},"GENERAL","libFolders"){
		@Override public void doAdditionalChecks(Object value) throws InvalidParameterException 
		{ for(File f: (File[])value) Files.checkForDir(f, true); }
	},
	FILES_NATIVE_LIST(COMMA_LIST,"missing_natives.a,NativeLib_beta.galaxy","GENERAL","nativeLibs"),

	FILES_MAP_IN(USER_FILE,null,"-i"),
	FILES_MAP_OUT(USER_FILE,null,"-o"),
	FILES_XML_STRUCTURE(USER_FILE,null,"-s"),
	FILES_XML_ERRORS(USER_FILE,null,"-e"),
	FILES_OUT_DIR(USER_FILE,Files.getAppFile("./out"),"-d") { public boolean mayBeNull(){ return false;}},
	FILES_MAP_SCRIPT_IN(USER_FILE,null,"-m"),
	FILES_MAP_TRIGGERS_IN(USER_FILE,null,"-t"),
	
	//Misc
	MISC_NO_CODE_GEN(FLAG,false,"--nocode"),
	XML_OUTPUT_NATIVES(FLAG,false,"--xmlnatives");
	
	private String section;
	private String key;
	private String clParam;
	
	private Object defaultValue;
	private ParamType type;
	
	private boolean runConfigDependant;
	
	private Parameter(ParamType t, Object def, String section, String key) {
		this(t,def,section,key,null);
	}
	
	private Parameter(ParamType t, Object def, String clParam) {
		this(t,def,null,null,clParam);
	}
	
	private Parameter(ParamType t, Object def, String section, String key, String clParam) {
		type = t;
		defaultValue = def;
		this.section = section;
		this.key = key;
		this.clParam = clParam;
		if(section!= null)
			runConfigDependant = section.startsWith(".");
	
	}
	
	public ParamType getType(){
		return type;
	}
	
	public String getCLParamString() {
		return clParam;
	}

	public String getSection(String runConfig) {
		if(runConfigDependant) return runConfig + section;
		return section;
	}

	public String getKey() {
		return key;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public void doAdditionalChecks(Object value) throws InvalidParameterException{
		
	}
	
	public boolean mayBeNull(){
		return true;
	}

	
}
