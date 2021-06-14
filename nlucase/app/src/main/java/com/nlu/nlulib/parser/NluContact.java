package com.nlu.nlulib.parser;

public class NluContact{
	
	private String name;
	
	
	
	
	/**
	 * 别名，用于做模糊匹配用
	 */
	private String alias;

	public NluContact(String name) {
		super();
		this.name = name;
		this.alias = name;
	}
	
	public NluContact(String name, String alias) {
		super();
		this.name = name;
		this.alias = alias;
	}

	public String getName() {
		if (name == null) 
			return "";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
