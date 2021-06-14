package com.nlu.nlulib.rule.validator;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluContextKey;
import com.nlu.nlulib.rule.NluRuleMatchResult;
import com.nlu.nlulib.rule.NluRules;
import com.nlu.nlulib.rule.NluValidator;

/**
 * 基于规则的validator，默认实现，只会根据规则来进行校验
 *
 */
public class NluDomainAndActionValidator implements NluValidator {
	
	private NluRules nluRules;
	
	public NluDomainAndActionValidator() {
		// TODO Auto-generated constructor stub
		nluRules = null;
	}

	/**
	 * return:
	 * 		true if match rules
	 * 		false if not 
	 */
	@Override
	public boolean validate(String value, NluContext context) {
		
		if (nluRules == null) {
			return true;
		}
		
		Object domainContext = context.get(NluContextKey.DOMAIN_CONTEXT);
		if (domainContext != null) {
			NluRuleMatchResult result = nluRules.match(domainContext.toString(), context, false);
			return result.isMatch();
		}
		return true;
	}

	@Override
	public void setRules(NluRules rules) {
		this.nluRules = rules;
	}

}
