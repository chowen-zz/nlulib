package com.nlu.nlulib.rule.result.handler;

import com.nlu.nlulib.parser.NluContactContext;
import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluResultHandler;
import com.nlu.nlulib.rule.NluRuleMatchResult;

import java.util.ArrayList;
import java.util.List;

public class ReturnAllContactHandler extends NluResultHandler {
	
	private final static String TYPE_KEY = "type";
	
	private final static String TYPE_NAME = "name";
	
	private final static String VALUE_KEY = "value";	
	
	public ReturnAllContactHandler() {
		
	}
	
	@Override
	public NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain) {
		
		if ( ! (context instanceof NluContactContext) || // context不是NluContact
				((NluContactContext)context).getOriginalContactList().isEmpty() ||// 联系人列表为空
				result.getResultMap().containsKey(TYPE_KEY)
		   )  
		{
			if (chain)  
				return chain(result, group);
		
			return result;
		}
		
		result.getResultMap().remove(group);
		
		NluContactContext nluContactContext = (NluContactContext)context;
		List<String> contactList = nluContactContext.getOriginalContactList();
		
		result.getResultMap().put(TYPE_KEY, TYPE_NAME);
		List<Object> valueList = new ArrayList<>();
		for (String contact : contactList) {
			valueList.add(contact);
		}
		result.getResultMap().put(VALUE_KEY, valueList);
		result.setMatch(true);
		return result;	
	}
	
}
