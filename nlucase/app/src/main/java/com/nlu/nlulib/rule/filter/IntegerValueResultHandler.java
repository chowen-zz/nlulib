package com.nlu.nlulib.rule.filter;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluResultHandler;
import com.nlu.nlulib.rule.NluRuleMatchResult;

/**
 * 匹配百分数的时候，正则表达式必须要把百分数标记，如"百分之"，"%"当成是value传入
 *
 */
public class IntegerValueResultHandler extends NluResultHandler {
	
	public NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain) {
		
		result.getResultMap().put(group, 1);
		
		return result;
	}
}
