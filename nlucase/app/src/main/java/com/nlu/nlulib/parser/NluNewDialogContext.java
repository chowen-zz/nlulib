package com.nlu.nlulib.parser;

public class NluNewDialogContext extends NluContext{
	
	private String domainActionName; 

	public NluNewDialogContext(String domainActionName, String dialogId) {
		super(dialogId);		
		this.domainActionName = domainActionName;
	}

	public String getDomainActionName() {
		return this.domainActionName;
	}
}
