package com.nlu.nlulib.rule.filter;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluFilter;
import com.nlu.nlulib.rule.NluRules;

public class DiscardFilter implements NluFilter {
	
	private NluRules nluRules;

	@Override
	public String filter(String input, NluContext context) {
		
		if (nluRules == null)
			return input;
		
		if (nluRules.match(input, context).isMatch()) {
			return "";
		}
		
		return input;
	}

	@Override
	public void setRules(NluRules rules) {
		this.nluRules = rules;		
	}

}
