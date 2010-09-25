package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.MessageSeverity;

public class ParseResult {

	private ArrayList<Message> warnings = new ArrayList<Message>();
	private ArrayList<Message> errors = new ArrayList<Message>();
	
	public ArrayList<Message> getWarnings() {
		return warnings;
	}
	public ArrayList<Message> getErrors() {
		return errors;
	}
	
	public void addWarning(Message warning){
		warnings.add(warning);
	}
	
	public void addError(Message error){
		errors.add(error);
	}
	
	
	public void addMessages(List<Message> messages) {
		for(Message m: messages){
			if(m.getSeverity()== MessageSeverity.WARNING){
				addWarning(m);
			} else if(m.getSeverity() == MessageSeverity.ERROR){
				addError(m);
			}
		}
	}
	
	public boolean isSuccessful(){
		return errors.isEmpty();
	}
	
	
}
