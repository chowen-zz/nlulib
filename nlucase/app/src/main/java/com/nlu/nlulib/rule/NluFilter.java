package com.nlu.nlulib.rule;


import com.nlu.nlulib.parser.NluContext;

public interface NluFilter {
	
	String filter(String input, NluContext context);
	
	
	void setRules(NluRules rules);
	
}
