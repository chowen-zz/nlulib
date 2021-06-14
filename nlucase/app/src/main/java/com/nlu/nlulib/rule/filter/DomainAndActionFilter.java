package com.nlu.nlulib.rule.filter;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluContextKey;
import com.nlu.nlulib.rule.NluFilter;
import com.nlu.nlulib.rule.NluRules;

public class DomainAndActionFilter implements NluFilter {
	
	private NluRules nluRules;

	@Override
	public String filter(String input, NluContext context) {
		
		if (nluRules == null)
			return input;
		
		Object domainContext = context.get(NluContextKey.DOMAIN_CONTEXT);
		if (domainContext == null)
			return input;
		
		if (nluRules.match(domainContext.toString(), context).isMatch()) {
			return "";
		}
		
		return input;
	}

	@Override
	public void setRules(NluRules rules) {
		this.nluRules = rules;		
	}

}
