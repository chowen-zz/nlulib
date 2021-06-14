package com.nlu.nlulib.rule.result.handler;

import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluResultHandler;
import com.nlu.nlulib.rule.NluRuleMatchResult;
import com.nlu.nlulib.rule.util.NluSelectIndexUtil;

import org.apache.commons.lang3.StringUtils;


/**
 * 匹配百分数的时候，正则表达式必须要把百分数标记，如"百分之"，"%"当成是value传入
 *
 */
public class SetToNumberResultHandler extends NluResultHandler {
	
	public NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain) {
		
		String text = (String)result.getResultMap().get(group);
		
		if (StringUtils.isEmpty(text) ) {
			return result;
		}
	
		if (text.indexOf("负") != -1 || text.indexOf("副") != -1 || text.indexOf("-") != -1|| text.indexOf("零点") != -1) {
			result.setMatch(false);
			return result;
		}
		
		// 匹配到的结果本身就已经是数字了
		//1. 如果匹配到数字，就直接返回数字本身
		if (StringUtils.isNumeric(text) ) {
			try {
				int value = Integer.parseInt(text);
				
				result.getResultMap().put(group, value);
				return result;
			}catch (Exception e) {
			}
		}
		
		int value = NluSelectIndexUtil.getSelectedIndex(text, context, false);
		
		//2. 如果匹配的是百分数，则以10为档，返回0-10
		if (text.indexOf("百分之") != -1 || text.indexOf("%") != -1) 
		{
			result.getResultMap().put(group, value);
			result.getResultMap().put("percent", true);
			return result;
		} 
		else  
		{//3. asr识别的结果是一个整数，getSelectedIndex识别成功后返回数字本身
			result.getResultMap().put(group, value);
			return result;
		}
	}
}
