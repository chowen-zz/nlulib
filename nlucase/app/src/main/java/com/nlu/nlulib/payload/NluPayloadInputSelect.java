package com.nlu.nlulib.payload;

/**
 * 输入文本或是选择的索引
 *
 */
public class NluPayloadInputSelect extends com.nlu.payload.NluPayload {

	/**
	 * 输入的文本
	 */
	private String inputText;
	
	/**
	 * index from 1, -1 mean backward index, zero means nothing is selected
	 */
	private Integer selectedIndex;

	
	public NluPayloadInputSelect(String inputText, Integer selectedIndex) {
		super();
		this.inputText = inputText;
		this.selectedIndex = selectedIndex;
	}
	
	
	public NluPayloadInputSelect(String inputText) {
		this(inputText, 0);
	}
	
	
	public NluPayloadInputSelect(Integer selectedIndex) {
		this("", selectedIndex);
	}
	

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(Integer selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
}
