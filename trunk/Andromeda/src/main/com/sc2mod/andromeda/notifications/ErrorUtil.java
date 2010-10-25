package com.sc2mod.andromeda.notifications;

import java.io.IOException;

import com.sc2mod.andromeda.util.Debug;

public final class ErrorUtil {

	//Util
	private ErrorUtil(){}
	
	public static InternalProgramError illegalSwitchValue(Object value){
		return new InternalProgramError("Illegal value " + String.valueOf(value));
	}
	
	public static UnrecoverableProblem raiseInternalProblem(Throwable t){
		String errorMessage = Debug.getStackTrace(t);
		throw Problem.ofType(ProblemId.INTERNAL_PROBLEM).details(errorMessage)
			.raiseUnrecoverable();
	}
	
	public static Problem raiseIOProblem(IOException t, boolean unrecoverable){
		Problem p = Problem.ofType(ProblemId.EXTERNAL_IO_EXCEPTION).details(t.getMessage());
		if(unrecoverable){
			throw p.raiseUnrecoverable();
		} else {
			return p.raise();
		}
	}
	
	public static Problem raiseExternalProblem(Throwable t, boolean unrecoverable){
		Problem p = Problem.ofType(ProblemId.EXTERNAL_EXCEPTION).details(t.getClass().getSimpleName(),t.getMessage());
		if(unrecoverable){
			throw p.raiseUnrecoverable();
		} else {
			return p.raise();
		}
	}
	
}
