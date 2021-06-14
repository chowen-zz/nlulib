package com.nlu.nlulib.rule.result.handler;


import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluResultHandler;
import com.nlu.nlulib.rule.NluRuleMatchResult;
import com.nlu.nlulib.rule.util.NluSelectIndexUtil;

public class SelectIndexResultHandler extends NluResultHandler {

	@Override
	public NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain) {
		
		String text = (String)result.getResultMap().get(group);
		
		int selectedIndex = NluSelectIndexUtil.getSelectedIndex(text, context);
        
        if (selectedIndex == 0) {
        	result.getResultMap().remove(group);
        } else {
        	result.getResultMap().put(group, selectedIndex);
        }
        
		return result;
	}

}
