package com.nlu.nlulib.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * NluDomain
 *
 */
public class NluDomain {
	
	private String name;
	
	private DomainType domainType;
	
	private int priority;
	
	// 单轮, other domains > system domain
	public static final int PRIORITY_DEFAULT = 10;
	
	public static final int PRIORITY_SYSTEM  = 8;
	// 多轮， system domain > internal domain
	public static final int PRIORITY_INTERNAL = 9;
	
	public static final int PRIORITY_SYSTEM_INTERNAL = 10;
	
	
	public enum DomainType {
		INTERNAL,
		EXTERNAL,
		BOTH
	}

	@Deprecated
	private List<NluDomainActionRules> nluDomainActionRules;
	
	public NluDomain(String name, DomainType domainType, int priority) {
		this.name = name;
		this.priority = priority;
		this.domainType = domainType; 
		nluDomainActionRules = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public DomainType getDomainType() {
		return domainType;
	}

	public void setDomainType(DomainType domainType) {
		this.domainType = domainType;
	}
}
