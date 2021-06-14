package com.nlu.nlulib.rule.result;

public class NluContactResult {

	public final static String TYPE_NEMO = "nemo";
	
	public final static String TYPE_PHONE = "phone";
	
	public final static String TYPE_NAME = "name";
	
	public final static String TYPE_PUBLIC = "public";
	
	private String type;
	
	private String value;
	
	private Double confidence;
	
	public NluContactResult(String type, String value, Double confidence) {
		super();
		this.type = type;
		this.value = value;
		this.confidence = confidence;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	
	
	
}
