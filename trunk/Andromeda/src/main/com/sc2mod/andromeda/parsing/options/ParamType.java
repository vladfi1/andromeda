package com.sc2mod.andromeda.parsing.options;

import java.io.File;
import java.io.IOException;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.ProblemResponse;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.util.Files;
import com.sc2mod.andromeda.util.Strings;

public enum ParamType {
	/**
	 * User files are files that are provided by the user.
	 * If a relative path is given for them, this relative path is searched in the current working directory.
	 */
	USER_FILE {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			return Files.getUserFile(s);
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(value == null) return null;
			if(!(value instanceof File)) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: File");
			return value;
		}
	},
	/**
	 * Application files are files that normally lie inside any subfolder of the andromeda installation.
	 * If a relative path is given for them, the base is the andromeda folder, not the CWD.
	 */
	APP_FILE {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			return Files.getAppFile(s);
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(value == null) return null;
			if(!(value instanceof File)) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: File");
			return value;
		}
	},
	APP_FILES {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			String[] fileNames = s.split(",");
			File[] files = new File[fileNames.length];
			for(int i = 0,length = fileNames.length;i<length;i++){
				files[i] = (File)Files.getAppFile(s);
			}
			return files;
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(value == null) return null;
			if(!(value instanceof File[])) 
				throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: File[]");
			return value;
		}
	},
	STRING {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			return s;
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(!(value instanceof String)) return String.valueOf(value);
			return value;
		}
	},
	INT {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				throw new InvalidParameterException("Invalid value '" + s + "'. Only integer numbers are allowed.");
			}
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(value == null) throw new InvalidParameterException("Cannot use null for an int-type parameter");
			if(!(value instanceof Integer)) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: Integer");
			return value;
		}
	},
	FLAG {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			return Boolean.parseBoolean(s);	
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(value == null) throw new InvalidParameterException("Cannot use null for an flag-type parameter");
			if(!(value instanceof Boolean)) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: Boolean");
			return value;
		}
	},
	PROBLEM_RESPONSE {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			try{
				return ProblemResponse.valueOf(s.toUpperCase());
			} catch(IllegalArgumentException iae){
				throw new InvalidParameterException("Invalid value '" + s + "' for a problem response. Allowed are " 
						+ Strings.mkString(ProblemResponse.values(), ","));
			}
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(!(value instanceof ProblemResponse)) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: ProblemResponse");
			return value;
		}
	},
	COMMA_LIST {
		@Override
		public Object parseString(String s) throws InvalidParameterException {
			return s.split(",");
		}
		@Override
		public Object checkValue(Object value) throws InvalidParameterException {
			if(!(value instanceof String[])) throw new InvalidParameterException("The value of type " + value.getClass().getSimpleName() 
					+ " does not match the type of the parameter. Type needed: String[]");
			return value;
		}
	};
	
	
	public abstract Object parseString(String s) throws InvalidParameterException;

	public abstract Object checkValue(Object value) throws InvalidParameterException;
}
