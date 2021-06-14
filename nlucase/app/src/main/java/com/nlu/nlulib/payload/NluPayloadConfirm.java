package com.nlu.nlulib.payload;

public class NluPayloadConfirm extends com.nlu.payload.NluPayload {

	private boolean confirm;

	public NluPayloadConfirm(boolean confirm) {
		super();
		this.confirm = confirm;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
}
