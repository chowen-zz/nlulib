package com.nlu.nlulib.parser;

import java.util.ArrayList;
import java.util.List;

public class NluContactContext extends NluContext{
	
	private List<NluContact> contactList;
	
	private List<String> originalContactList;
	
	public NluContactContext(List<String> contacts, String dialogId) {
		super(dialogId);
		
		originalContactList = contacts;

		contactList = new ArrayList<>();
		
		for (String name : contacts) {
			if (name == null || name.length() == 0)
				continue;
			
			this.contactList.add(new NluContact(name));
			if (name.indexOf("我的") == -1)
				this.contactList.add(new NluContact(name, "我的" + name));	
		}
	}		


	public List<NluContact> getContactList() {
		return contactList;
	}

	
	public void setContactList(List<NluContact> contactList) {
		this.contactList = contactList;
	}
	
	public List<String> getOriginalContactList() {
		return this.originalContactList;
	}

}
