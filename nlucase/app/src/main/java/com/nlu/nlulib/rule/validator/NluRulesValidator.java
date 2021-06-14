package com.nlu.nlulib.rule.validator;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluRuleMatchResult;
import com.nlu.nlulib.rule.NluRules;
import com.nlu.nlulib.rule.NluValidator;

/**
 * 基于规则的validator，默认实现，只会根据规则来进行校验
 *
 */
public class NluRulesValidator implements NluValidator {
	
	private NluRules nluRules;
	
	public NluRulesValidator() {
		nluRules = null;
	}

	/**
	 * return:
	 * true if match rules，false if not
	 */
	@Override
	public boolean validate(String value, NluContext context) {
		
		if (nluRules == null) {
			return true;
		}
		NluRuleMatchResult result = nluRules.match(value, context,
				false);
		return result.isMatch();
	}

	@Override
	public void setRules(NluRules rules) {
		this.nluRules = rules;
	}

}
