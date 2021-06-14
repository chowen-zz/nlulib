package com.nlu.nlulib;

import java.util.Collections;
import java.util.Map;

/**
 * Defines a general command to be executed
 * 
 * nlucommand has no setter methods
 */
public class NluCommand {
    /**
     * domain of this command, e.g., "phone", "message", "system"
     * 
     * @see com.nlu.nlulib.NluDomainName
     */
    private String domain;
    
    /**
     * action of current domain
     */
    private String action;
    
    /**
     * 对话id，多轮对话时，dialogId相同
     */
    private String dialogId;

    /**
     * payload of this command, it can be empty for many case
     */
    //private NluPayload payload;
    private Map<String, Object> payload;
    
    
    public final static NluCommand NONE = new NluCommand("", "", "", Collections.EMPTY_MAP);
    /**
     * constructor
     * 
     * @param domain
     * @param action
     * @param dialogId
     * @param payload
     */
    public NluCommand(String domain, String action, String dialogId, Map<String, Object> payload) {
		this.domain = domain;
		this.action = action;
		
		this.payload = payload;
		
		this.dialogId = dialogId;		
	}
    
	public NluCommand(String domain, String action, Map<String, Object> payload) {
		this(domain, action, System.currentTimeMillis() + "", payload);		
	}
	
	public NluCommand(String domain, String action) {
		this(domain, action, Collections.EMPTY_MAP);
	}

	public String getDomain() {
		return domain;
	}

	public String getAction() {
		return action;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}

	public String getDialogId() {
		return dialogId;
	}

}
