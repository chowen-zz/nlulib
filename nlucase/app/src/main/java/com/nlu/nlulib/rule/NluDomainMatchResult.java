package com.nlu.nlulib.rule;

import com.nlu.nlulib.NluCommand;

import java.util.Map;


public class NluDomainMatchResult {
	
	private String domain;
	
	private String action;
	
	private NluRuleMatchResult ruleMatchResult;
	
	
	public final static  NluDomainMatchResult EMPTY_DOMAIN_MATCH_RESULT = new NluDomainMatchResult("", "", NluRuleMatchResult.emptyResult());

	public NluDomainMatchResult(String domain, String action, NluRuleMatchResult ruleMatchResult) {
		super();
		this.domain = domain;
		this.action = action;
		this.ruleMatchResult = ruleMatchResult;
	}
	
	public static NluDomainMatchResult emptyResult() {
		return EMPTY_DOMAIN_MATCH_RESULT;
	}
	
	public boolean isMatch() {
		return ruleMatchResult.isMatch();
	}
	
	public Map<String, Object> getResultMap() {
		return ruleMatchResult.getResultMap();
	}
	
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getAction() {
		return this.action;
	}
	
	
	public NluCommand toNluCommand() {
		return new NluCommand(domain, action, ruleMatchResult.getResultMap());
	}
	
	public NluCommand toNluCommand(String dialogId) {
		return new NluCommand(domain, action, dialogId, ruleMatchResult.getResultMap());
	}
}
