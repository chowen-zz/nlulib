package com.nlu.nlulib.rule;

public class NluRuleValidateConfig {
	
	private String group;
	
	private NluValidator validator;
	
	private boolean pass;

	public NluRuleValidateConfig(String group, NluValidator validator, boolean pass) {
		super();
		this.group = group;
		this.validator = validator;
		this.pass = pass;
	}

	
	public NluRuleValidateConfig(String group, NluValidator validator) {
		this(group, validator, true);
	}


	public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public NluValidator getValidator() {
		return validator;
	}


	public void setValidator(NluValidator validator) {
		this.validator = validator;
	}


	public boolean isPass() {
		return pass;
	}


	public void setPass(boolean pass) {
		this.pass = pass;
	}
	
}
