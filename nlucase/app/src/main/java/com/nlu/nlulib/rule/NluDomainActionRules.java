package com.nlu.nlulib.rule;

import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluContextKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NluDomainActionRules {
	/**
	 * @see com.nlu.nlulib.NluDomainName
	 */
	private NluDomain nluDomain;
	
	/**
	 * @see com.nlu.nlulib.NluDomainAction
	 */
	private String action;
	
	
	/**
	 * 当前domain Action依赖的 action
	 */
	private Set<String> dependActionSet; 
	
	
	/**
	 * rules for current domain
	 */
	private List<NluRules> rules;
	
	/**
	 * 优先级
	 */
	private int priority;
	
	
	/**
	 * 能否被其他domain引用，用于在SYSTEM垂类里面定义
	 */
	private boolean refered;
	
	
	public final static int DEFAULT_PRIORITY = 10;
	

	public NluDomainActionRules(NluDomain domain, String action, int priority, boolean refered) {
		super();
		this.nluDomain = domain;
		this.action = action;
		this.priority = priority;
		this.refered = refered;
		
		rules = new ArrayList<>();
		//
	}
	
	public NluDomainMatchResult match(String input, NluContext context) {
		
		NluRuleMatchResult ruleMatchResult = NluRuleMatchResult.emptyResult();
		
		String domainContext = (String)context.get(NluContextKey.DOMAIN_CONTEXT);
		
		for (NluRules rule : rules) {
			
			if (this.hasDepends() && !this.depends(domainContext) ) {
				continue;
			}
			
			if (rule.isReplace()) {
				
				input = rule.replace(input, context);
				
			} else {
				
				ruleMatchResult = rule.match(input, context);
				
				if (ruleMatchResult.isMatch()) {
					return new NluDomainMatchResult(nluDomain.getName(), action, ruleMatchResult);
				}	
			}
		}
		
		return NluDomainMatchResult.emptyResult();
	}

	public String getDomainName() {
		return nluDomain.getName();
	}

	
	public NluDomain getDomain() {
		return nluDomain;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public List<NluRules> getRules() {
		return rules;
	}

	public void addRules(NluRules nluRules) {
		this.rules.add(nluRules);
	}
	
	/**
	 * 支持domain间依赖的话，rulesName为domainName.actionName
	 * 
	 * @param rulesName
	 */
	public void addDependAction(String rulesName) {
		
		if (dependActionSet == null) {
			dependActionSet = new HashSet<>();
		}
		
		this.dependActionSet.add(rulesName);
	}
	
	public boolean hasDepends() {
		return dependActionSet != null && dependActionSet.size() > 0;
	}
	
	/**
	 * 支持domain间依赖的，rulesName为domainName.actionName
	 * 
	 * @param actionName
	 * @return
	 */
	public boolean depends(String domainActionName) {
		if (dependActionSet == null || dependActionSet.size() == 0) {
			return false;
		}
		return this.dependActionSet.contains(domainActionName);
	}
	
	
	/**
	 * 支持domain间依赖的，rulesName为domainName.actionName
	 * 
	 * @param actionName
	 * @return
	 */
	public boolean depends(String domainName, String actionName) {
		return this.dependActionSet.contains(domainName + "." + actionName);
	}
	
	
	public boolean isDependentAction() {
		return this.dependActionSet.size() != 0;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int comparePriority(NluDomainActionRules domainActionRules) {
		int prior = this.priority - domainActionRules.getPriority();
		
		if (prior == 0) {
			return this.getDomain().getPriority() - domainActionRules.getDomain().getPriority();
		}
		
		return prior;
	}
	
	public boolean canRefered() {
		return this.refered;
	}
}
