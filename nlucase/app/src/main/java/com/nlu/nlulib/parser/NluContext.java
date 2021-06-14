package com.nlu.nlulib.parser;

import java.util.HashMap;
import java.util.Map;

public class NluContext {
	
	private String dialogId;
	
	// 延时创建
	private Map<String, Object> ext;
	
	public final static String None = null; 
	
	public final static String SINGLE_ROUND_DIALOG = null;
	
	public final static String CLASS_NAME = "NluContext";
	
	public NluContext(String dialogId) {
		super();
		this.dialogId = dialogId;
	}
	
	public static NluContext newDialog(String domainName) {
		NluContext context = new NluNewDialogContext(domainName, System.currentTimeMillis()+"");
		return context;
	}

	public void set(String key, Object value) {
		
		if (ext == null) {
			ext = new HashMap<String, Object>();
		}
		ext.put(key, value);	
	}
	
	public void remove(String key) {
		
		if (ext != null) {
			ext.remove(key);	
		}
	}
	
	public Object get(String key)  {
		if (ext == null) {
			return null;
		}
		return ext.get(key);
	}


	public String getDialogId() {
		return dialogId;
	}


	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}
}
