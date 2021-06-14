package com.nlu.nlulib.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class NluRuleMatchResult {

	private boolean match;
	
	
	private Map<String, Object> resultMap;
	

	public final static NluRuleMatchResult NLU_EMPTY_MATCH_RESULT = new NluRuleMatchResult(false, Collections.EMPTY_MAP);
	
	
	public NluRuleMatchResult(boolean match) {
		this(match, Collections.EMPTY_MAP);
	}
	
	public NluRuleMatchResult(boolean match, String key, Object value) {
		this.match = match;
		
		if (StringUtils.isEmpty(key)) {
			resultMap = Collections.emptyMap();
		} else {
			resultMap = new HashMap<>();
			
			resultMap.put(key, value);
		}
	}

	public NluRuleMatchResult(boolean match, Map<String, Object> resultMap) {
		super();
		this.match = match;
		this.resultMap = resultMap;
	}

	public static NluRuleMatchResult emptyResult() {
		return NLU_EMPTY_MATCH_RESULT;
	}

	public boolean isMatch() {
		return match;
	}


	public void setMatch(boolean match) {
		this.match = match;
	}


	public Map<String, Object> getResultMap() {
		return resultMap;
	}


	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	
	
	public void remove(String group) {
		if (resultMap.size() > 0) {
			resultMap.remove(group);
		}
	}
	
}
