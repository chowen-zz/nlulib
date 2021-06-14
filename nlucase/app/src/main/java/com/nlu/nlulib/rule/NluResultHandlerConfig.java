package com.nlu.nlulib.rule;

public class NluResultHandlerConfig {
	
	private NluResultHandler resultHandler;
	
	
	private String group;
	
	
	private boolean chain; 
	

	public NluResultHandlerConfig(NluResultHandler resultHandler, String group, boolean chain) {
		super();
		this.resultHandler = resultHandler;
		this.group = group;
		this.chain = chain;
	}


//	public NluResultHandlerConfig(NluResultHandler resultHandler, String group) {
//		this(resultHandler, group, false);
//	}


	public NluResultHandler getResultHandler() {
		return resultHandler;
	}


	public void setResultHandler(NluResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}


	public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public boolean isChain() {
		return chain;
	}


	public void setChain(boolean chain) {
		this.chain = chain;
	}
	
	
	
}
